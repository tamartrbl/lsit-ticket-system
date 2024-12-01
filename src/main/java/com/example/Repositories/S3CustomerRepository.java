package com.example.Repositories;

import java.net.URI;
import java.util.*;

import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.Models.Customer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;


@Primary
@Repository
public class S3CustomerRepository implements ICustomerRepository { //create interface for all the repositories implemented
    // final String BUCKET="lsit-ticketing-bucket";
    // final String PREFIX="customers/";
    // final String ACCESS_KEY="GOOG1EXMBPGJHUOYOKG6RKYIDDRZY72XC7NOMR46UAKZON5KZSW6SSYJ4WNGK";
    // final String SECRET_KEY="geOsRgqiViIlx+TBlY/eIjJXiC7E5+a88pIlOoOL";
    // final String ENDPOINT_URL="https://storage.googleapis.com";

    final String BUCKET="ticketingsystem123";
    final String PREFIX="ticketing/customers/";
    final String ACCESS_KEY="GOOG1ESDISC2L36V7ZM66BWRXCGSVRWN3TLKCVGKOSPEO5EDFZH7JQ6R3NIOC";
    final String SECRET_KEY="RMjmuRZnitoVLndGKCDGVGu91QvTnJ68FUdld4Et";
    final String ENDPOINT_URL="https://storage.googleapis.com";


    S3Client s3client;
    AwsCredentials awsCredentials;
        

        public S3CustomerRepository() {
            try {
                awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
                s3client = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .region(Region.of("auto"))
                    .build();
                System.out.println("S3Client successfully initialized.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize S3Client", e);
            }
        }
    
        public void add(Customer c){
            try{
                c.id = UUID.randomUUID();
    
                ObjectMapper om = new ObjectMapper();
    
                String customerJson = om.writeValueAsString(c);
                
                s3client.putObject(PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(PREFIX + c.id.toString())
                    .build(),
                    RequestBody.fromString(customerJson)
                );
            }
            catch(JsonProcessingException e){
                e.printStackTrace(); // Log the stack trace for debugging
            throw new RuntimeException("Error serializing Customer object", e); // Optional: throw runtime exception
            }
        }
    
        public Customer get(UUID id){
            try{
                var objectBytes = s3client.getObject(GetObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(PREFIX + id.toString())
                    .build()
                ).readAllBytes();
    
                ObjectMapper om = new ObjectMapper();
                Customer c = om.readValue(objectBytes, Customer.class);
    
                return c;
            }catch(Exception e){
                return null;
            }
        }
    
        public void remove(UUID id){
            s3client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(PREFIX + id.toString())
                .build()
            );  
        }
    
        public void update(Customer c){
            try{
                Customer x = this.get(c.id);
                if(x == null) return;
    
                ObjectMapper om = new ObjectMapper();
                String petJson = om.writeValueAsString(c);
                s3client.putObject(PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(PREFIX + c.id.toString())
                    .build(),
                    RequestBody.fromString(petJson)
                );
            }
            catch(JsonProcessingException e){}
        }
    
        public List<Customer> list(){
            List<Customer> pets = new ArrayList<Customer>();
            List<S3Object> objects = s3client.listObjects(ListObjectsRequest.builder()
              .bucket(BUCKET)
              .prefix(PREFIX)
              .build()  
            ).contents();
    
            for(S3Object o : objects){
                Customer c = new Customer();
                //p = this.get(UUID.fromString(o.key().substring(PREFIX.length())));
                c.id = UUID.fromString(o.key().substring(PREFIX.length()));
                pets.add(c);
            }
    
            return pets;
        }
    
}
package com.example.Controllers;


import java.net.http.HttpClient;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {
    String userInfo;

    @GetMapping("/")
    public ResponseEntity get(){
        return ResponseEntity.ok("something!");
    }

    @GetMapping("/authorise")
    public RedirectView getUser(OAuth2AuthenticationToken authentication){
        // var groups = (List<String>)authentication.getPrincipal().getAttribute("groups");
        // return groups.get(0);

        var userAttributes = authentication.getPrincipal().getAttributes();
        userInfo = "<pre> \n" +
            userAttributes.entrySet().parallelStream().collect(
                StringBuilder::new,
                (s, e) -> s.append(e.getKey()).append(": ").append(e.getValue()),
                (a, b) -> a.append("\n").append(b)
            ) +
            "</pre>";


        // StringBuilder sb = new StringBuilder();
        // for(var entry : userAttributes.entrySet()){
        //     var s = entry.getKey() + ": " + entry.getValue();
        //     sb.append("\n").append(s);
        // }
        // session.setAttribute("userInfo", userInfo);
        return new RedirectView("user");
    }

    @GetMapping("/user")
    public String getUserInfo(){
        // var groups = (List<String>)authentication.getPrincipal().getAttribute("groups");
        // return groups.get(0);

        // StringBuilder sb = new StringBuilder();
        // for(var entry : userAttributes.entrySet()){
        //     var s = entry.getKey() + ": " + entry.getValue();
        //     sb.append("\n").append(s);
        // }

        // String userInfo = (String) session.getAttribute("userInfo");
        // if (userInfo == null) {
        //     return "No user information found. Please authorise first.";
        // }
        return userInfo;
    }

}

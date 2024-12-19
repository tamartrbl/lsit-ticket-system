FROM docker.io/maven:3-eclipse-temurin-22 AS build

CMD mkdir /app
WORKDIR /app

COPY . /app

RUN mvn clean package



FROM docker.io/eclipse-temurin:22-jre

RUN mkdir /app
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar



EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]
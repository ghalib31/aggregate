# FedEx Assignment

## Pre-requisite
Docker should be installed  
Run below to download the image for backend services
```bash
docker pull xyzassessment/backend-services
```
Run below to start docker container for backend service
```bash
docker run -p 8080:8080 -d --name tnt xyzassessment/backend-services
```
*Note: Above command starts services on port 8080.  
If you wish to use any other port, please change it in aggregate.properties as well.*

<br>

## Building and running from code
Go to the root folder of the code i.e. aggregate (in terminal/command prompt)and run (use mvnw.cmd for windows)
```bash
sh ./mvnw clean install
sh ./mvnw spring-boot:run
```

<br>

## Running from jar
Go to the location of the jar (in terminal/command prompt) and run
```bash
java -jar aggregate-0.0.1-SNAPSHOT.jar
```
*Note: Application is configured to run on port 8888.  
If you wish to use any other port, please change it in application.properties and build again``.*

<br>

Example url to hit:  
http://localhost:8888/aggregation?pricing=NL,CN&track=109347263,123456891&shipments=109347263,123456891
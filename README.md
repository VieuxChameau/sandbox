# Sandbox

Just to showcase/play around with spring boot, kotlin, java 13, docker, kubernetes

## Table of contents
- [Prerequisites](#Prerequisites)
- [Compilation](#Compilation)
    - [Maven](#Maven)
        - [To build a fat jar](#To-build-a-fat-jar)
        - [To build a war](#To-build-a-war)
        - [To add extra info to the build info in jenkins](#To-add-extra-info-to-the-build-info-in-jenkins)
    - [Gradle](#Gradle)
        - [To build a fat jar](#To-build-a-fat-jar)
        - [To build a war](#To-build-a-war)
        - [To add extra info to the build info in jenkins](#To-add-extra-info-to-the-build-info-in-jenkins)
- [Running everything with docker-compose](#Running-everything-with-docker-compose)
- [Running the app outside docker](#Running-the-app-outside-docker)
    - [Running the war](#Running-the-war)
    - [Running the fat jar](#Running-the-fat-jar)
    - [Running with Spring Boot Maven plugin](#Running-with-Spring-Boot-Maven-plugin)
    - [Running with Spring Boot Gradle plugin](#Running-with-Spring-Boot-Gradle-plugin)
- [Endpoints](#Endpoints)
- [Creating Key pair](#Creating-Key-pair)

### Prerequisites

* Java 13
* Docker to run the project locally
* Optional : get a free access key for the [currencies exchange api](https://currencylayer.com/). The application will still run without it

If you use IntelliJ and want to use the maven wrapper you need to install the plugin Maven Wrapper support prior to opening the project

### Compilation

Choice to use either maven or gradle wrapper

#### Maven

Two profiles are available :
* jar : will build a fat jar
* war : will build a war deployable in tomcat

##### To build a fat jar

```shell
./mvnw clean package -Pjar
```

##### To build a war

```shell
./mvnw clean package -Pwar
```

##### To add extra info to the build info in jenkins

```shell
./mvnw clean package -Pwar -Djenkins.buildTag=${BUILD_TAG}
```
This work with both profile.
*BUILD_TAG is one of the environment variables set by jenkins for each build*

#### Gradle

##### To build a fat jar
```shell
./gradlew bootJar
```

##### To build a war

```shell
./gradlew bootWar
```

##### To add extra info to the build info in jenkins
```shell
./gradlew build -DjenkinsBuildTag=${BUILD_TAG}
```

*BUILD_TAG is one of the environment variables set by jenkins for each build*

### Running everything with docker-compose

You can build and run the app and its dependencies with docker-compose

```shell
cd docker
docker-compose -f docker-compose.yml up --build -d
```

If you want to debug the application run

```shell
cd docker
docker-compose -f docker-compose.yml -f docker-compose-debug.yml up --build -d
```

Then attach the debugger to the remote JVM on port 8000

*If you are using Windows you may have to run the Docker Desktop and powershell as administrator, so the processes
can create the ports they need.*

### Running the app outside docker

First you need to run the dependencies : rabbitmq, redis 

```shell
cd docker
docker-compose -f docker-compose-local.yml up -d
```

#### Running the war

If you have selected the profile war during the build using either maven or gradle, you need to deploy the war to tomcat.

#### Running the fat jar

If you have build a fat jar by using either maven or gradle

```shell
java --enable-preview -jar sandbox-0.0.1-SNAPSHOT.jar --spring.profiles.active=local-docker --sandbox.exchangesrates.api.token=theRealToken
```

#### Running with Spring Boot Maven plugin

If you have selected the profile jar during the build, you can run with the following commands:

```shell
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=local-docker,--sandbox.exchangesrates.api.token=theRealToken
```

#### Running with Spring Boot Gradle plugin

```shell
./gradlew bootRun --args='--spring.profiles.active=local-docker --sandbox.exchangesrates.api.token=theRealToken'
```

### Endpoints

Spring actuator is activated and some endpoints are available

* [See application health information](http://localhost:8080/actuator/health)
* [Displays info about the application. Version, Java Version](http://localhost:8080/actuator/info)
* [View some metrics](http://localhost:8080/actuator/metrics)
* [Api Documentation (Swagger)](http://localhost:8080/swagger-ui.html)

### Creating Key pair

A key is used to sign the JWT Token, in case you want to create a new key, the following commands can be used.

To create a new key pair in a keystore
```shell
keytool -genkeypair -alias sandboxJwtKey -keyalg RSA -keypass SandboxSecurityFirst -keystore sandboxJwtKeystore.jks -storepass SandboxSecurityFirst
```

To extract the public key

```shell
# First extract the public certificate
keytool -export -keystore sandboxJwtKeystore.jks -alias sandboxJwtKey -file sandboxJwtKey.cer
 
# Then extract the public key from the certificate
openssl x509 -inform der -in sandboxJwtKey.cer -pubkey -noout > sandboxJwtPublicKey.pub
```

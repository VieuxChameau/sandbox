# Sandbox

Just to showcase/play around with spring boot, kotlin, java 12, docker, kubernetes

### Prerequisites

* Java 12
* Docker to run the project locally

If you use IntelliJ and want to use the maven wrapper you need to install the plugin Maven Wrapper support prior to opening the project

### Compilation

#### Maven

Two profiles are available :
* jar : will build a fat jar
* war : will build a war deployable in tomcat

##### To build a fat jar

```
./mvnw clean package -Pjar
```

##### To build a war

```
./mvnw clean package -Pwar
```

##### To add extra info to the build info in jenkins

```
./mvnw clean package -Pwar -Djenkins.buildTag=${BUILD_TAG}
```
This work with both profile

#### Gradle

##### To build a fat jar
```
./gradlew bootJar
```

##### To build a war

```
./gradlew bootWar 
```

##### To add extra info to the build info in jenkins
```
./gradlew build -DjenkinsBuildTag=$BUILD_TAG
```

### Running locally

First you need to run the dependencies : rabbitmq, redis 

```
cd docker
docker-compose up
```

If you are using Windows you may have to run the Docker Desktop and powershell as administrator, so the process
can create the ports they need.

####  Running the war

If you have selected the profile war during the build using either maven or gradle, you need to deploy the war to tomcat.

#### Running the fat jar

If you have build a fat jar by using either maven or gradle

```
java -jar sandbox-0.0.1-SNAPSHOT.jar --spring.profiles.active=local-docker --sandbox.exchangesrates.api.token=theRealToken
```

#### Running with Spring Boot Maven plugin

If you have selected the profile jar during the build, you can run with the following commands:

```
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=local-docker,--sandbox.exchangesrates.api.token=theRealToken
```

#### Running with Spring Boot Gradle plugin

```
./gradlew bootRun --args='--spring.profiles.active=local-docker --sandbox.exchangesrates.api.token=theRealToken'
```

### Endpoints

Spring actuator is activated and some endpoints are available

* [See application health information](http://localhost:8080/actuator/health)
* [Displays info about the application. Version, Java Version](http://localhost:8080/actuator/info)
* [View some metrics](http://localhost:8080/actuator/metrics)
* [Api Documentation (Swagger)](http://localhost:8080/swagger-ui.html)

### Creating Key pair

To create a new key pair in a keystore
```
keytool -genkeypair -alias sandboxJwtKey -keyalg RSA -keypass SandboxSecurityFirst -keystore sandboxJwtKeystore.jks -storepass SandboxSecurityFirst
```

To extract the public key

```
# First extract the public certificate
keytool -export -keystore sandboxJwtKeystore.jks -alias sandboxJwtKey -file sandboxJwtKey.cer
 
# Then extract the public key from the certificate
openssl x509 -inform der -in sandboxJwtKey.cer -pubkey -noout > sandboxJwtPublicKey.pub
```

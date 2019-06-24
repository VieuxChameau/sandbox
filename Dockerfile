FROM adoptopenjdk/openjdk12:latest AS build
WORKDIR /app
COPY ./ ./
RUN ./mvnw clean package -Pjar

FROM adoptopenjdk/openjdk12-openj9:alpine-jre
WORKDIR /app
COPY --from=build /app/target/sandbox-0.0.1-SNAPSHOT.jar sandbox.jar
ENTRYPOINT ["java", "-jar", "sandbox.jar"]
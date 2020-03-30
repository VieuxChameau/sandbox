FROM adoptopenjdk/openjdk13:latest AS build
WORKDIR /app
COPY ./ ./
RUN ./mvnw clean package -Pjar

FROM adoptopenjdk/openjdk13-openj9:alpine-jre
WORKDIR /app
COPY --from=build /app/target/sandbox-0.0.1-SNAPSHOT.jar sandbox.jar
ENTRYPOINT exec java $JAVA_OPTS  -jar sandbox.jar
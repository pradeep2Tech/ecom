# Multi-stage Dockerfile: build with Maven, run on Alpine with Java 25
# Uses a build stage with Maven and a small Alpine-based JDK 25 runtime

FROM maven:3.10-jdk-25 AS build
WORKDIR /workspace
COPY pom.xml mvnw mvnw.cmd ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM bellsoft/liberica-openjdk-alpine:25
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

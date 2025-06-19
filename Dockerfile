# Dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/proj-5-1.0.0.jar app.jar
EXPOSE 1011
ENTRYPOINT ["java", "-jar", "app.jar"]

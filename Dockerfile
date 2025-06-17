# Use official Java 17 runtime base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the jar file into the container
COPY target/proj-5-1.0.0.jar app.jar

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Use a minimal OpenJDK image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Give Maven wrapper execution permission
RUN chmod +x mvnw

# Download project dependencies (optional but speeds up image builds)
RUN ./mvnw dependency:go-offline -B

# Copy the rest of your source code
COPY src/ src/

# Build the application and create the fat jar (skip tests for faster build)
RUN ./mvnw package -DskipTests && cp target/*.jar app.jar

# Expose the port your Spring Boot app runs on (change if different)
EXPOSE 8080

# Set the entry point to run the app
CMD ["java", "-jar", "app.jar"]

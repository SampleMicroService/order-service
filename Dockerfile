# Use a base image with a suitable Java version
FROM openjdk:17

# Expose the port your Spring Boot application listens on (optional for Lambda, common for testing)
EXPOSE 8082

# Add the JAR file to the container (copying it to the root directory here)
ADD target/order-service-0.0.1-SNAPSHOT.jar /order-service-0.0.1-SNAPSHOT.jar

# Set the entry point to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/order-service-0.0.1-SNAPSHOT.jar"]

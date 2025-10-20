FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the compiled JAR file into the container
COPY target/lms-1.0.0.jar app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

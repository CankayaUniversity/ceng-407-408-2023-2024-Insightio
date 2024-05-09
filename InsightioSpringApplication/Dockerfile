# Use the official Maven image as the base image for building the application
FROM maven:latest AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file to the working directory
COPY pom.xml .

# Copy the source code to the working directory
COPY src ./src

# Build the application using Maven
RUN mvn clean package

# Use the official OpenJDK image as the base image for running the application
FROM openjdk:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file from the build stage to the working directory
COPY --from=build /app/target/*.jar ./app.jar

# Expose the port on which your Spring Boot application runs (8091)
EXPOSE 8091

# Command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]

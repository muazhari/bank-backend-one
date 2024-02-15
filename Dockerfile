# Get the base image for building the application
FROM gradle:jdk21 AS builder

# Copy the current directory contents into the container at /app
COPY . /app

# Set the working directory
WORKDIR /app

# Build the application
RUN gradle build --no-daemon


# Get the base image for running the application
FROM openjdk:21

# Copy the current directory contents into the container at /app
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Set the working directory
WORKDIR /app



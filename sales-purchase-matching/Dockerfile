FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml for dependency resolution
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Fix Spring Boot version in pom.xml
RUN sed -i 's/<version>3.4.3<\/version>/<version>3.2.3<\/version>/' pom.xml

# Fix the annotation processor path issue
RUN sed -i '/<path>/,/<\/path>/c\        <path>\n          <groupId>org.projectlombok<\/groupId>\n          <artifactId>lombok<\/artifactId>\n          <version>${lombok.version}<\/version>\n        <\/path>' pom.xml


# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080

# Expose the port the app runs on
EXPOSE 8080

# At the end of your Dockerfile, before the ENTRYPOINT
# RUN echo "PORT is set to: $PORT"
#ENTRYPOINT ["sh", "-c", "echo \"Starting application on port $PORT\" && java -jar app.jar --server.port=$PORT"]
# Ensure the app binds to 0.0.0.0:$PORT (required for Cloud Run)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=0.0.0.0:$PORT"]
# Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

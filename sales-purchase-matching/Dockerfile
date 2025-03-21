FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml for dependency resolution
COPY pom.xml .
# Copy source code
COPY src ./src

# Fix Spring Boot version in pom.xml
RUN sed -i 's/<version>3.4.3<\/version>/<version>3.2.3<\/version>/' pom.xml

# Fix the annotation processor path issue
RUN sed -i '/<path>/,/<\/path>/c\        <path>\n          <groupId>org.projectlombok<\/groupId>\n          <artifactId>lombok<\/artifactId>\n          <version>${lombok.version}<\/version>\n        <\/path>' pom.xml


# Build the application
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# At the end of your Dockerfile, before the ENTRYPOINT
RUN echo "PORT is set to: $PORT"
EXPOSE ${PORT:-8080}
ENTRYPOINT ["sh", "-c", "echo \"Starting application on port ${PORT:-8080}\" && java -jar app.jar"]

# Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

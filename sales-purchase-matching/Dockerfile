# FROM maven:3.9-eclipse-temurin-17 AS build

# WORKDIR /app

# # Copy pom.xml for dependency resolution
# COPY pom.xml .
# RUN mvn dependency:go-offline

# # Copy source code
# COPY src ./src

# # Fix Spring Boot version in pom.xml
# RUN sed -i 's/<version>3.4.3<\/version>/<version>3.2.3<\/version>/' pom.xml

# # Fix the annotation processor path issue
# RUN sed -i '/<path>/,/<\/path>/c\        <path>\n          <groupId>org.projectlombok<\/groupId>\n          <artifactId>lombok<\/artifactId>\n          <version>${lombok.version}<\/version>\n        <\/path>' pom.xml

# # Build the application
# RUN mvn clean package -DskipTests

# # Run stage
# FROM eclipse-temurin:17-jre

# WORKDIR /app

# # Copy the built JAR file
# COPY --from=build /app/target/*.jar app.jar

# ENV PORT=8080

# # Expose the port the app runs on
# EXPOSE 8080

# # Ensure the app binds to 0.0.0.0:$PORT
# ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.address=0.0.0.0 --server.port=$PORT"]

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml for dependency resolution
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Fix Spring Boot version in pom.xml
RUN sed -i 's/<version>3.4.3<\/version>/<version>3.2.3<\/version>/' pom.xml

# Fix the annotation processor path issue with a hardcoded version
RUN sed -i '/<path>/,/<\/path>/c\        <path>\n          <groupId>org.projectlombok<\/groupId>\n          <artifactId>lombok<\/artifactId>\n          <version>1.18.30<\/version>\n        <\/path>' pom.xml

# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Create a startup script with better diagnostics
RUN echo '#!/bin/sh\n\
echo "Container starting..."\n\
echo "PORT=$PORT"\n\
echo "JAVA_OPTS=$JAVA_OPTS"\n\
java -Dserver.port=$PORT -Dserver.address=0.0.0.0 -Dspring.profiles.active=cloud -jar app.jar\n' > /app/start.sh && \
chmod +x /app/start.sh

# Expose port 8080 (for documentation, doesn't actually publish the port)
EXPOSE 8080

# Use the startup script as entrypoint
ENTRYPOINT ["/app/start.sh"]

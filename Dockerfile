
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
COPY pom.xml  ./

RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
# docker build -t spring-boot-app .
# docker run -p 8080:8080 spring-boot-app
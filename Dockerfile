# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

# Download dependencies first (uses Docker cache)
RUN mvn dependency:go-offline

COPY src ./src

# Limit Maven JVM memory
ENV MAVEN_OPTS="-Xms256m -Xmx768m"

RUN mvn clean package -DskipTests -T 1

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar --server.port=${PORT}"]
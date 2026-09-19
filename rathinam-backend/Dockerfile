# ==============================================================================
# Build Stage: Maven + Temurin JDK 21 Alpine
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Pre-fetch dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==============================================================================
# Runtime Stage: Minimal Temurin JRE 21 Alpine
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Add unprivileged user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy compiled jar from build stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

# Configure container-aware JVM memory tuning
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

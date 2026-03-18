# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build with Maven
# ─────────────────────────────────────────────────────────────────────────────
# maven:3.9-eclipse-temurin-21-alpine ships with Maven pre-installed — no wget
# bootstrap needed, which avoids download failures in restricted Docker environments.
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml ./

# Download dependencies before copying source — maximises Docker layer cache.
# If only source code changes, this layer is reused and the download is skipped.
RUN mvn dependency:go-offline -q

# Copy source and build the fat JAR (skip tests — they can run in CI)
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Runtime — minimal JRE image (no JDK/Maven in production)
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# The OPENAI_API_KEY must be supplied via docker-compose / docker run --env.
# It is NOT baked into the image.
ENV OPENAI_API_KEY=""

EXPOSE 8080

# Use exec form to ensure proper signal handling (graceful shutdown)
ENTRYPOINT ["java", "-jar", "app.jar"]

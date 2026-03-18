# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build with Maven
# ─────────────────────────────────────────────────────────────────────────────
# eclipse-temurin is the recommended OpenJDK distribution and has both
# linux/amd64 (Intel/Ubuntu) and linux/arm64 (Apple Silicon) images.
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper first (cached unless wrapper version changes)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies before copying source — maximises Docker layer cache.
# If only source code changes, this layer is reused and the download is skipped.
RUN ./mvnw dependency:go-offline -q

# Copy source and build the fat JAR (skip tests — they can run in CI)
COPY src ./src
RUN ./mvnw clean package -DskipTests -q

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

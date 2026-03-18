# ─────────────────────────────────────────────────────────────────────────────
# Pre-built image: the fat JAR is built on the host before running docker build.
#   mvn clean package -DskipTests
# Docker only packages the result — no Maven/network access required.
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy the pre-built fat JAR from the host
COPY target/*.jar app.jar

# The OPENAI_API_KEY must be supplied via docker-compose / docker run --env.
# It is NOT baked into the image.
ENV OPENAI_API_KEY=""

EXPOSE 8080

# Use exec form to ensure proper signal handling (graceful shutdown)
ENTRYPOINT ["java", "-jar", "app.jar"]

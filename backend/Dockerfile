# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build with Maven
# ─────────────────────────────────────────────────────────────────────────────
# maven:3.9-eclipse-temurin-21-alpine ships with Maven pre-installed — no wget
# bootstrap needed, which avoids download failures in restricted Docker environments.
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy the pre-downloaded Maven repository (built with: mvn dependency:go-offline -Dmaven.repo.local=./.m2)
# This avoids any network access during the Docker build.
COPY .m2 .m2
COPY pom.xml ./
COPY src ./src

# Build fully offline using the bundled local repo
RUN mvn clean package -DskipTests -q -Dmaven.repo.local=.m2 --offline

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

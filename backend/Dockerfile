# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage build — no Maven or Java required on the host.
#
# Stage 1: build the fat JAR inside Docker using Maven
# Stage 2: copy only the JAR into a slim JRE image
#
# Works on macOS (Apple Silicon & Intel) and Ubuntu Linux.
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy dependency descriptors first so Docker can cache the Maven layer
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /build/target/*.jar app.jar

# The OPENAI_API_KEY must be supplied via docker-compose / docker run --env.
# It is NOT baked into the image.
ENV OPENAI_API_KEY=""

EXPOSE 8080

# Use exec form to ensure proper signal handling (graceful shutdown)
ENTRYPOINT ["java", "-jar", "app.jar"]

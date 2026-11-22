# syntax=docker/dockerfile:1
# Healthcare Operations Dashboard: Vue frontend + Spring Boot API in one image.
#
#   docker build -t healthcare-dashboard .
#   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=demo healthcare-dashboard
#
# Without SPRING_PROFILES_ACTIVE=demo the app connects to MySQL using DB_HOST, DB_PORT,
# DB_NAME, DB_USER and DB_PASSWORD (see docker-compose.yml).

# ---- 1. Build the Vue frontend --------------------------------------------------------
FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
# vite.config.js writes the build to ../target/classes/static (i.e. /app/target/classes/static).
RUN npm run build

# ---- 2. Build the Spring Boot jar -----------------------------------------------------
FROM maven:3.9-eclipse-temurin-26 AS backend
WORKDIR /app
COPY pom.xml ./
# Download dependencies in their own layer so code changes don't re-download them.
RUN mvn -B -q -DskipFrontend dependency:go-offline
COPY src ./src
COPY database ./database
COPY --from=frontend /app/target/classes/static ./target/classes/static
# Tests already run in CI; the frontend was built in stage 1.
RUN mvn -B -q -DskipFrontend -DskipTests -Djacoco.skip=true package

# ---- 3. Small runtime image -----------------------------------------------------------
FROM eclipse-temurin:21-jre
RUN groupadd --system app && useradd --system --gid app --uid 10001 app
WORKDIR /app
COPY --from=backend /app/target/healthops.jar app.jar
USER app
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

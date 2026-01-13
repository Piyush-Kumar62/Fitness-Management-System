# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Cache dependencies (speeds up builds significantly)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 2. Build the application
COPY src ./src
RUN mvn -B clean package -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:21-jre-jammy

# 3. Security: Create a non-root user to run the app
RUN useradd -m springuser
USER springuser

WORKDIR /app

# 4. Copy the jar using a wildcard to handle version changes easily
COPY --from=build /app/target/*.jar app.jar

# 5. Networking & Performance
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
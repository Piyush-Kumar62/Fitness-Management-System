# Fitness Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

**REST API for fitness activity tracking with JWT authentication and recommendation storage.**

[API Docs](http://localhost:8080/swagger-ui.html) • [LinkedIn](https://www.linkedin.com/in/piyush-kumar62/)

</div>

---

## Implementation Details

**Authentication:** JWT tokens with JJWT 0.12.x, BCrypt password hashing, Spring Security filter chain  
**Database:** PostgreSQL/MySQL with Hibernate, JSON columns for metrics storage  
**Build:** Maven 3.9.9, Java 21, multi-stage Docker build  
**API Docs:** SpringDoc OpenAPI 3.0 auto-generated from controller annotations  

---

## Tech Stack

| Layer | Technology | Version/Details |
|-------|-----------|-----------------|
| **Runtime** | Java | 21 (Eclipse Temurin) |
| **Framework** | Spring Boot | 3.x |
| **Security** | Spring Security + JWT | JJWT 0.12.x, BCrypt |
| **ORM** | Spring Data JPA | Hibernate 6.x |
| **Database** | PostgreSQL / MySQL | JSON column support |
| **Connection Pool** | HikariCP | Max 5 connections |
| **Build** | Maven | 3.9.9 |
| **Container** | Docker | Multi-stage build |
| **Utilities** | Lombok | Annotations for boilerplate |
| **Validation** | Bean Validation | Jakarta Validation API |
| **Docs** | SpringDoc OpenAPI | 3.0 spec |

---

## Architecture

```
Request → JwtAuthenticationFilter → SecurityFilterChain
            ↓
       Controller (REST endpoints)
            ↓
       Service (business logic)
            ↓
       Repository (JPA)
            ↓
       Database (PostgreSQL/MySQL)
```

**Implementation:**
- **Controllers:** `AuthController`, `ActivityController`, `RecommendationController`
- **Services:** `UserService`, `ActivityService`, `RecommendationService`
- **Repositories:** JPA interfaces with custom query methods (`findByUser_Id`, `findByEmail`)
- **Security:** `SecurityConfig` configures filter chain, `JwtAuthenticationFilter` validates tokens
- **DTOs:** Separate request/response objects for each endpoint

---

## Features Implemented

### Authentication (`AuthController`)
```
POST /api/auth/register  → UserService.register() → BCrypt hash → Save to DB
POST /api/auth/login     → UserService.authenticate() → JwtUtils.generateToken() → Return JWT
```
- Password hashing: BCrypt with default strength (10 rounds)
- Token generation: HMAC-SHA256, configurable expiration (default 24h)
- Token validation: Custom `JwtAuthenticationFilter` on every request

### Activity Tracking (`ActivityController`)
```
POST /api/activities      → ActivityService.trackActivity() → Save Activity entity
GET  /api/activities      → ActivityService.getUserActivities() → Find by userId
GET  /api/activities/{id} → ActivityService.getActivityById() → Find by ID
```
- Activity types: `RUNNING`, `WALKING`, `CYCLING`, `SWIMMING`, `WEIGHT_TRAINING`, `YOGA`, `HIIT`, `CARDIO`, `STRETCHING`, `OTHER`
- Stored fields: `type`, `duration` (seconds), `caloriesBurned`, `startTime`, `additionalMetrics` (JSON)
- JSON column: Stores arbitrary key-value pairs (distance, heart rate, etc.)

### Recommendations (`RecommendationController`)
```
POST /api/recommendations/generate         → RecommendationService.generateRecommendation()
GET  /api/recommendations/user/{userId}    → findByUser_IdOrderByCreatedAtDesc()
GET  /api/recommendations/activity/{activityId} → findByActivity_IdOrderByCreatedAtDesc()
```
- Linked to: User (FK) and Activity (FK)
- JSON arrays: `improvements`, `suggestions`, `safety` (List<String>)
- Sorting: Descending by `createdAt`

---

## Database Schema

**users**
```sql
id              UUID PRIMARY KEY
email           VARCHAR UNIQUE NOT NULL
password        VARCHAR(60) NOT NULL  -- BCrypt hash
first_name      VARCHAR
last_name       VARCHAR
role            VARCHAR(20)           -- USER or ADMIN
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

**activity**
```sql
id                  UUID PRIMARY KEY
user_id             UUID FOREIGN KEY → users(id)
type                VARCHAR(50)        -- Enum: RUNNING, WALKING, etc.
duration            INTEGER            -- Seconds
calories_burned     INTEGER
start_time          TIMESTAMP
additional_metrics  JSON               -- Flexible key-value storage
created_at          TIMESTAMP
updated_at          TIMESTAMP
```

**recommendation**
```sql
id              UUID PRIMARY KEY
user_id         UUID FOREIGN KEY → users(id)
activity_id     UUID FOREIGN KEY → activity(id)
type            VARCHAR
recommendation  VARCHAR(2000)
improvements    JSON               -- Array of strings
suggestions     JSON               -- Array of strings
safety          JSON               -- Array of strings
created_at      TIMESTAMP
updated_at      TIMESTAMP
```

**Relationships:**
- User → Activity: OneToMany (cascade ALL, orphanRemoval)
- User → Recommendation: OneToMany (cascade ALL, orphanRemoval)
- Activity → Recommendation: OneToMany

---

## API Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'

# Returns: {"token": "eyJhbGci...", "user": {...}}
```

### Track Activity
```bash
curl -X POST http://localhost:8080/api/activities \
  -H "Authorization: Bearer eyJhbGci..." \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uuid-here",
    "type": "RUNNING",
    "duration": 3600,
    "caloriesBurned": 450,
    "startTime": "2026-01-19T08:00:00",
    "additionalMetrics": {
      "distance": 10.5,
      "avgHeartRate": 145
    }
  }'
```

### Get User Activities
```bash
curl http://localhost:8080/api/activities \
  -H "Authorization: Bearer eyJhbGci..."
```

### Generate Recommendation
```bash
curl -X POST http://localhost:8080/api/recommendations/generate \
  -H "Authorization: Bearer eyJhbGci..." \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uuid-here",
    "activityId": "activity-uuid",
    "type": "Performance",
    "recommendation": "Increase interval training",
    "improvements": ["Add hill sprints", "Track heart rate zones"],
    "suggestions": ["Rest 48h between sessions"],
    "safety": ["Warm up 10 minutes", "Stay hydrated"]
  }'
```

---

## Security Configuration

**JWT Implementation (`JwtUtils.java`):**
- Algorithm: HMAC-SHA256
- Secret: Base64-encoded, minimum 64 bytes
- Expiration: 86400000ms (24 hours)
- Claims: `subject` (userId), `roles` (array)

**Security Filter Chain (`SecurityConfig.java`):**
```java
Public:     /api/auth/**, /swagger-ui/**, /v3/api-docs/**, /actuator/health
USER role:  /api/activities/**, /api/recommendations/**
ADMIN role: /actuator/** (except /health)
```

**Filter Order:**
1. `CorsFilter` (configured for `http://localhost:4200`)
2. `JwtAuthenticationFilter` (custom, validates token)
3. `UsernamePasswordAuthenticationFilter` (Spring default)

**CORS Settings:**
- Allowed Origins: `http://localhost:4200` (configurable via `ALLOWED_ORIGINS`)
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed Headers: Authorization, Content-Type

---

## Setup

**Requirements:**
- Java 21
- Maven 3.9+
- PostgreSQL 15+ or MySQL 8+

**Configuration (`.env` file):**
```bash
DB_URL=jdbc:postgresql://localhost:5432/fitness_db
DB_USER=postgres
DB_PWD=yourpassword
JWT_SECRET=<base64-encoded-64-byte-string>
JWT_EXPIRATION=86400000
ALLOWED_ORIGINS=http://localhost:4200
PORT=8080
```

**Run Locally:**
```bash
# Create database
createdb fitness_db

# Clone repo
git clone https://github.com/Piyush-Kumar62/Fitness-Management-System.git
cd Fitness-Management-System

# Create .env file (see above)

# Run application
./mvnw spring-boot:run

# Verify
curl http://localhost:8080/actuator/health
# Output: {"status":"UP"}
```

**Access Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

---

## Docker

**Dockerfile Structure:**
```dockerfile
# Stage 1: Build (maven:3.9.9-eclipse-temurin-21)
- Copy pom.xml → mvn dependency:go-offline
- Copy src → mvn clean package -DskipTests
- Output: target/*.jar

# Stage 2: Runtime (eclipse-temurin:21-jre-jammy)
- Create user: springuser (non-root)
- Copy JAR from build stage
- JVM flags: -XX:+UseContainerSupport -XX:MaxRAMPercentage=75
- Expose: 8080
```

**Build & Run:**
```bash
docker build -t fitness-api .

docker run -d -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/fitness_db \
  -e DB_USER=postgres \
  -e DB_PWD=password \
  -e JWT_SECRET=yourBase64Secret \
  -e JWT_EXPIRATION=86400000 \
  fitness-api
```

**Docker Compose:**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: fitness_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports: ["5432:5432"]
    volumes: [postgres_data:/var/lib/postgresql/data]

  fitness-api:
    build: .
    ports: ["8080:8080"]
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/fitness_db
      DB_USER: postgres
      DB_PWD: postgres
      JWT_SECRET: <your-secret>
    depends_on: [postgres]

volumes:
  postgres_data:
```

```bash
docker-compose up -d
```

---

## Project Structure

```
src/main/java/com/project/fitness/
│
├── config/
│   └── OpenApiConfig.java                  # Swagger configuration
│
├── controller/
│   ├── AuthController.java                 # /api/auth/** endpoints
│   ├── ActivityController.java             # /api/activities/** endpoints
│   └── RecommendationController.java       # /api/recommendations/** endpoints
│
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── UserResponse.java
│   ├── ActivityRequest.java
│   ├── ActivityResponse.java
│   ├── RecommendationRequest.java
│   └── RecommendationResponse.java
│
├── model/
│   ├── User.java                           # @Entity with @Table(name="users")
│   ├── UserRole.java                       # Enum: USER, ADMIN
│   ├── Activity.java                       # @Entity with JSON column
│   ├── ActivityType.java                   # Enum: RUNNING, WALKING, etc.
│   └── Recommendation.java                 # @Entity with 3 JSON columns
│
├── repository/
│   ├── UserRepository.java                 # findByEmail()
│   ├── ActivityRepository.java             # findByUser_Id()
│   └── RecommendationRepository.java       # findByUser_IdOrderByCreatedAtDesc()
│
├── security/
│   ├── SecurityConfig.java                 # Filter chain configuration
│   ├── JwtUtils.java                       # Token generation/validation
│   ├── JwtAuthenticationFilter.java        # OncePerRequestFilter
│   └── CustomUserDetailsService.java       # UserDetailsService implementation
│
├── service/
│   ├── UserService.java                    # register(), authenticate()
│   ├── ActivityService.java                # trackActivity(), getUserActivities()
│   └── RecommendationService.java          # generateRecommendation()
│
└── FitnessManagementSystemApplication.java # @SpringBootApplication

src/main/resources/
└── application-dev.properties              # Environment-based config

Dockerfile                                  # Multi-stage build
pom.xml                                     # Maven dependencies
```

---

## Key Files

**pom.xml dependencies:**
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-oauth2-resource-server`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (0.12.x)
- `mysql-connector-j`, `postgresql`
- `springdoc-openapi-starter-webmvc-ui`
- `spring-boot-starter-actuator`
- `lombok`

**application-dev.properties:**
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=true
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=30000
management.endpoints.web.exposure.include=health,info
```

---

## Monitoring

**Actuator Endpoints:**
```bash
GET /actuator/health      # Public (always accessible)
GET /actuator/info        # Requires ADMIN role
GET /actuator/metrics     # Requires ADMIN role (if exposed)
```

**Health Check Response:**
```json
{"status":"UP"}
```

---

## Design Patterns Used

| Pattern | Implementation | File |
|---------|---------------|------|
| **Repository** | JPA interfaces extend `JpaRepository` | `*Repository.java` |
| **DTO** | Separate request/response classes | `dto/*.java` |
| **Builder** | Lombok `@Builder` on entities | `@Builder` annotation |
| **Filter Chain** | Custom JWT filter in security chain | `JwtAuthenticationFilter.java` |
| **Dependency Injection** | Constructor-based injection | All services/controllers |
| **Service Layer** | Business logic separation | `service/*.java` |

---

## Code Examples

**Entity with JSON Column:**
```java
@Entity
public class Activity {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> additionalMetrics;
}
```

**JWT Token Generation:**
```java
public String generateToken(String userId, String role) {
    return Jwts.builder()
        .subject(userId)
        .claim("roles", List.of(role))
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key())
        .compact();
}
```

**Custom Repository Query:**
```java
public interface ActivityRepository extends JpaRepository<Activity, String> {
    List<Activity> findByUser_Id(String userId);
}
```

**Security Configuration:**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

---

## Testing

**Run Tests:**
```bash
./mvnw test
```

**Test File:** `FitnessManagementSystemApplicationTests.java` (context load test)

---

## License

MIT License

---

## Author

**Piyush Kumar**

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github)](https://github.com/Piyush-Kumar62)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=flat-square&logo=linkedin)](https://www.linkedin.com/in/piyush-kumar62/)
[![Email](https://img.shields.io/badge/Email-EA4335?style=flat-square&logo=gmail&logoColor=white)](mailto:piyushkumar30066@gmail.com)

---

<div align="center">

Built with Spring Boot 3 & Java 21 • JWT Authentication • PostgreSQL • Docker

</div>

# 🏋️ Fitness Management System

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-20-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.5-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Neon](https://img.shields.io/badge/Neon_DB-Serverless-00E699?style=for-the-badge)](https://neon.tech/)

**A full-stack web application for fitness activity tracking with AI-powered recommendations, secure JWT authentication, and real-time progress monitoring**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Quick Start](#-quick-start) • [Live Demo](#-deployment) • [API Docs](#-api-endpoints)

</div>

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [System Architecture](#-system-architecture)
- [Prerequisites](#-prerequisites)
- [Local Development Setup](#-local-development-setup)
- [Running Locally](#-running-locally)
- [Project Structure](#-project-structure)
- [API Endpoints](#-api-endpoints)
- [Security Implementation](#-security-implementation)
- [Database Configuration](#-database-configuration)
- [Deployment Options](#-deployment-options)
- [Screenshots](#-screenshots)
- [Future Enhancements](#-future-enhancements)
- [Contact](#-contact)

---

## 🎯 Project Overview

The **Fitness Management System** is a modern full-stack web application built to demonstrate enterprise-level software development skills. This project showcases proficiency in backend API development, frontend framework implementation, database design, authentication/authorization, and cloud deployment strategies.

### 🎨 Key Highlights for Recruiters

- ✅ **Full-Stack Development**: Complete end-to-end implementation from database to UI
- ✅ **Modern Tech Stack**: Spring Boot 3.3.5 + Angular 20 + MySQL/Neon DB
- ✅ **RESTful API Design**: Well-structured endpoints with proper HTTP methods
- ✅ **Security Best Practices**: JWT tokens, password encryption, SQL injection prevention
- ✅ **Responsive UI**: Mobile-friendly design with Tailwind CSS
- ✅ **Cloud-Ready**: Deployable on AWS with Neon DB integration
- ✅ **Code Quality**: Clean architecture, SOLID principles, comprehensive error handling
- ✅ **Version Control**: Git workflow with meaningful commits

### 💡 What This Project Demonstrates

- Backend development with Spring Boot (REST APIs, Spring Security, JPA)
- Frontend development with Angular (Components, Services, Routing, Guards)
- Database design and management (MySQL local, Neon DB cloud)
- Authentication & Authorization (JWT tokens, role-based access)
- API integration and HTTP communication
- State management and reactive programming
- Deployment strategies (Docker, AWS-ready)

---

## ✨ Features

### 👤 User Features

- **Secure Authentication**: Registration and login with JWT token-based authentication
- **Activity Logging**: Track various workout types (Running, Cycling, Swimming, Gym, Yoga, etc.)
- **Calorie Tracking**: Monitor calories burned for each activity session
- **AI Recommendations**: Receive personalized fitness suggestions based on activity history
- **Profile Management**: Update personal information and change password securely
- **Activity Dashboard**: Visualize workout history and progress with interactive charts
- **Responsive Design**: Access from any device - desktop, tablet, or mobile

### 👨‍💼 Admin Features

- **User Management**: View and manage all registered users
- **Activity Monitoring**: Oversee all user activities and system usage
- **System Health**: Monitor application status and performance
- **Role-Based Access**: Secure admin-only endpoints and features

### 🔧 Technical Features

- **RESTful API**: Clean, documented API endpoints following REST principles
- **Input Validation**: Server-side validation using Jakarta Bean Validation
- **Exception Handling**: Centralized error handling with user-friendly messages
- **CORS Configuration**: Secure cross-origin resource sharing
- **Database Abstraction**: JPA/Hibernate for database-agnostic operations
- **Environment-Based Config**: Separate configurations for development and deployment

---

## 🛠️ Tech Stack

### Backend

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 21
- **Database (Local)**: MySQL 8.0
- **Database (Cloud)**: Neon DB (PostgreSQL-compatible serverless)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT (HS512 algorithm)
- **Password Hashing**: BCrypt (strength 10)
- **Validation**: Jakarta Bean Validation (JSR 380)
- **API Documentation**: Springdoc OpenAPI 3.0 (Swagger UI)
- **Build Tool**: Apache Maven 3.8+

### Frontend

- **Framework**: Angular 20 (Standalone Components)
- **Language**: TypeScript 5.5+
- **Styling**: Tailwind CSS 3.4
- **HTTP Client**: Angular HttpClient with Interceptors
- **State Management**: Angular Signals (reactive state)
- **Form Handling**: Reactive Forms with custom validators
- **Routing**: Angular Router with Auth Guards
- **Icons**: Heroicons
- **Build Tool**: Angular CLI

### Database Options

- **Local Development**: MySQL 8.0 (localhost)
- **Cloud Deployment**: Neon DB (Serverless PostgreSQL)
- **Future**: AWS RDS (planned migration)

### DevOps & Deployment

- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions (planned)
- **Cloud Platform**: AWS (EC2, S3, RDS) - planned
- **Version Control**: Git & GitHub

---

## 🏗️ System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     Client Layer                             │
│         Angular 20 SPA (http://localhost:4200)              │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│   │  Auth Pages │  │  Dashboard  │  │   Profile   │       │
│   │ Login/Signup│  │  Activities │  │  Settings   │       │
│   └─────────────┘  └─────────────┘  └─────────────┘       │
└────────────────────────┬─────────────────────────────────────┘
                         │ HTTP/REST + JWT Token
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    API Layer                                 │
│      Spring Boot REST API (http://localhost:8080)           │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│   │ Auth Filter  │  │  Controllers │  │   Services   │    │
│   │ (JWT Valid.) │  │ (Endpoints)  │  │ (Logic)      │    │
│   └──────────────┘  └──────────────┘  └──────────────┘    │
│                         │                                    │
│   ┌─────────────────────▼────────────────────┐             │
│   │     Spring Security Layer                │             │
│   │  - JWT Authentication                    │             │
│   │  - BCrypt Password Encoding              │             │
│   │  - Role-Based Authorization (USER/ADMIN) │             │
│   └──────────────────────────────────────────┘             │
└────────────────────────┬─────────────────────────────────────┘
                         │ JPA/Hibernate (Parameterized Queries)
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                   Database Layer                             │
│  ┌──────────────────┐           ┌──────────────────┐        │
│  │  MySQL 8.0       │    OR     │   Neon DB        │        │
│  │  (Local Dev)     │           │ (Cloud Deployment)│        │
│  │  localhost:3306  │           │  Serverless PG   │        │
│  └──────────────────┘           └──────────────────┘        │
│                                                              │
│  Tables: users | activities | recommendations               │
└──────────────────────────────────────────────────────────────┘
```

**Security Flow:**

1. User submits credentials → Angular sends to `/api/auth/login`
2. Spring Security validates → BCrypt checks password hash
3. JWT token generated (24-hour expiry) → Returned to client
4. Client stores token → Sent in Authorization header for all requests
5. JWT Filter validates token → Extracts user info → Authorizes request
6. All database queries use JPA (parameterized) → SQL injection protected

---

## 📦 Prerequisites

Ensure you have the following installed:

| Tool         | Version | Download                                                          |
| ------------ | ------- | ----------------------------------------------------------------- |
| **Java JDK** | 21+     | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **Node.js**  | 18+     | [Node.js Official](https://nodejs.org/)                           |
| **npm**      | 9+      | Included with Node.js                                             |
| **MySQL**    | 8.0+    | [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)         |
| **Maven**    | 3.8+    | [Apache Maven](https://maven.apache.org/download.cgi)             |
| **Git**      | Latest  | [Git SCM](https://git-scm.com/downloads)                          |

**Optional:**

- **Docker** (for containerized deployment): [Docker Desktop](https://www.docker.com/get-started)

**Verify installations:**

```bash
java -version    # Should show Java 21+
node -v          # Should show v18+
npm -v           # Should show v9+
mysql --version  # Should show 8.0+
mvn -version     # Should show 3.8+
git --version    # Should show latest
```

---

## 🚀 Local Development Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/Piyush-Kumar62/Fitness-Management-System.git
cd Fitness-Management-System
```

### Step 2: Database Setup (MySQL)

**Option A: Using MySQL Command Line**

```sql
CREATE DATABASE fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'fitness_user'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON fitness_db.* TO 'fitness_user'@'localhost';
FLUSH PRIVILEGES;
```

**Option B: Using MySQL Workbench**

1. Open MySQL Workbench
2. Create new schema named `fitness_db`
3. Create user with appropriate privileges

### Step 3: Backend Configuration

```bash
cd backend

# Copy environment template
cp .env.example .env
```

**Edit `backend/.env` file:**

```env
# Database Configuration (LOCAL - MySQL)
DB_URL=jdbc:mysql://localhost:3306/fitness_db?useSSL=false&serverTimezone=UTC
DB_USER=fitness_user
DB_PWD=StrongPassword123!

# JWT Security Configuration
JWT_SECRET=MySecureJWTSecretKeyWith256BitsMinimum32Characters
JWT_EXPIRATION=86400000

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:4200

# Server Configuration
PORT=8080
```

**🔒 Security Notes:**

- ✅ Database credentials stored in `.env` (NOT tracked by Git)
- ✅ `.gitignore` prevents `.env` from being committed
- ✅ Never commit actual credentials to version control
- ✅ Use strong, unique passwords
- ✅ JWT secret must be at least 256 bits (32+ characters)

### Step 4: Frontend Configuration

```bash
cd ../frontend

# Copy environment template
cp .env.example .env
```

**Edit `frontend/.env` file:**

```env
# Backend API URL
API_URL=http://localhost:8080/api
```

---

## ▶️ Running Locally

### Start Backend Server

**Terminal 1:**

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

**Windows:**

```cmd
cd backend
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

✅ Backend running at: `http://localhost:8080`

**Verify:**

- Health Check: http://localhost:8080/actuator/health
- API Docs: http://localhost:8080/swagger-ui.html

### Start Frontend Server

**Terminal 2:**

```bash
cd frontend
npm install
npm start
```

✅ Frontend running at: `http://localhost:4200`

### Access Application

Open browser: **http://localhost:4200**

**Test Accounts:**

- Create a new account via the registration page
- All passwords are securely hashed with BCrypt

---

## 📁 Project Structure

```
Fitness-Management-System/
│
├── backend/                              # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/project/fitness/
│   │   │   │   ├── FitnessManagementSystemApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java          # Spring Security setup
│   │   │   │   │   └── OpenApiConfig.java           # Swagger configuration
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java          # /api/auth/* endpoints
│   │   │   │   │   ├── UserController.java          # /api/users/* endpoints
│   │   │   │   │   ├── ActivityController.java      # /api/activities/* endpoints
│   │   │   │   │   └── RecommendationController.java
│   │   │   │   ├── dto/                             # Data Transfer Objects
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── ActivityRequest.java
│   │   │   │   │   └── *Response.java
│   │   │   │   ├── exceptions/                      # Custom exceptions
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   ├── BadRequestException.java
│   │   │   │   │   └── UnauthorizedException.java
│   │   │   │   ├── model/                           # JPA Entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Activity.java
│   │   │   │   │   └── Recommendation.java
│   │   │   │   ├── repository/                      # Data Access Layer
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── ActivityRepository.java
│   │   │   │   │   └── RecommendationRepository.java
│   │   │   │   ├── security/
│   │   │   │   │   ├── JwtUtils.java                # JWT token generation/validation
│   │   │   │   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   │   │   │   └── CustomUserDetailsService.java
│   │   │   │   └── service/                         # Business Logic
│   │   │   │       ├── UserService.java
│   │   │   │       ├── ActivityService.java
│   │   │   │       └── RecommendationService.java
│   │   │   └── resources/
│   │   │       ├── application.properties           # Main config (uses env vars)
│   │   │       ├── application-dev.properties       # Local development
│   │   │       └── application-prod.properties      # Deployment config
│   │   └── test/                                    # Unit & Integration tests
│   ├── .env.example                                 # Environment template (SAFE)
│   ├── .gitignore                                   # Git ignore (.env excluded)
│   ├── pom.xml                                      # Maven dependencies
│   ├── Dockerfile                                   # Docker image config
│   └── mvnw, mvnw.cmd                              # Maven wrapper
│
├── frontend/                             # Angular 20 Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/                                # Core module
│   │   │   │   ├── guards/
│   │   │   │   │   ├── auth.guard.ts               # Route protection
│   │   │   │   │   └── admin.guard.ts              # Admin route guard
│   │   │   │   ├── interceptors/
│   │   │   │   │   ├── auth.interceptor.ts         # JWT token injection
│   │   │   │   │   └── error.interceptor.ts        # Error handling
│   │   │   │   ├── models/
│   │   │   │   │   ├── user.model.ts
│   │   │   │   │   ├── activity.model.ts
│   │   │   │   │   └── recommendation.model.ts
│   │   │   │   └── services/
│   │   │   │       ├── auth.service.ts             # Authentication logic
│   │   │   │       ├── user.service.ts             # User API calls
│   │   │   │       ├── activity.service.ts         # Activity API calls
│   │   │   │       ├── recommendation.service.ts
│   │   │   │       ├── api.service.ts              # Base HTTP service
│   │   │   │       ├── toast.service.ts            # Notifications
│   │   │   │       └── storage.service.ts          # LocalStorage wrapper
│   │   │   ├── features/                            # Feature modules
│   │   │   │   ├── auth/
│   │   │   │   │   ├── login/                      # Login component
│   │   │   │   │   └── register/                   # Register component
│   │   │   │   ├── dashboard/                      # User dashboard
│   │   │   │   ├── activities/
│   │   │   │   │   ├── activity-list/             # Activity list component
│   │   │   │   │   └── activity-form/             # Add/Edit activity
│   │   │   │   ├── profile/                        # User profile
│   │   │   │   └── admin/                          # Admin dashboard
│   │   │   ├── layouts/
│   │   │   │   ├── user-layout/                    # User layout wrapper
│   │   │   │   └── admin-layout/                   # Admin layout wrapper
│   │   │   ├── shared/                              # Shared components
│   │   │   │   ├── navbar/
│   │   │   │   ├── footer/
│   │   │   │   └── loader/
│   │   │   ├── app.routes.ts                        # Route configuration
│   │   │   └── app.ts                               # Root component
│   │   ├── environments/
│   │   │   ├── environment.ts                       # Development config
│   │   │   └── environment.prod.ts                  # Deployment config
│   │   ├── styles.scss                              # Global styles
│   │   └── index.html                               # Main HTML
│   ├── .env.example                                 # Environment template (SAFE)
│   ├── .gitignore                                   # Git ignore (.env excluded)
│   ├── package.json                                 # npm dependencies
│   ├── angular.json                                 # Angular CLI config
│   ├── tailwind.config.js                          # Tailwind CSS config
│   ├── tsconfig.json                               # TypeScript config
│   └── Dockerfile                                   # Docker image config
│
├── .gitignore                            # Root Git ignore
├── README.md                             # This file
├── SECURITY.md                           # Security documentation
└── SECURITY_AUDIT.md                     # Security audit report
```

**🔐 Security Notes:**

- All `.env` files are in `.gitignore` - never committed to Git
- Only `.env.example` files are tracked (safe templates)
- Database credentials are environment variables only
- JWT secrets are never hardcoded

---

## 🔌 API Endpoints

### Authentication Endpoints

| Method | Endpoint             | Description                 | Auth Required |
| ------ | -------------------- | --------------------------- | ------------- |
| POST   | `/api/auth/register` | Register new user account   | ❌            |
| POST   | `/api/auth/login`    | Login and receive JWT token | ❌            |

**Example Request:**

```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Example Response:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": "user-uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

### User Management Endpoints

| Method | Endpoint                     | Description              | Auth Required   |
| ------ | ---------------------------- | ------------------------ | --------------- |
| GET    | `/api/users/profile`         | Get current user profile | ✅ Bearer Token |
| PUT    | `/api/users/profile`         | Update user profile      | ✅ Bearer Token |
| POST   | `/api/users/change-password` | Change password          | ✅ Bearer Token |
| GET    | `/api/users/{id}`            | Get user by ID           | ✅ Admin Only   |
| GET    | `/api/users`                 | Get all users            | ✅ Admin Only   |
| DELETE | `/api/users/{id}`            | Delete user              | ✅ Admin Only   |

### Activity Endpoints

| Method | Endpoint               | Description             | Auth Required   |
| ------ | ---------------------- | ----------------------- | --------------- |
| POST   | `/api/activities`      | Create new activity     | ✅ Bearer Token |
| GET    | `/api/activities`      | Get all user activities | ✅ Bearer Token |
| GET    | `/api/activities/{id}` | Get activity by ID      | ✅ Bearer Token |

**Example Request:**

```json
POST /api/activities
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
{
  "type": "RUNNING",
  "duration": 45,
  "caloriesBurned": 350,
  "date": "2026-01-28T10:00:00"
}
```

### Recommendation Endpoints

| Method | Endpoint                                     | Description                  | Auth Required   |
| ------ | -------------------------------------------- | ---------------------------- | --------------- |
| POST   | `/api/recommendations/generate`              | Generate AI recommendation   | ✅ Bearer Token |
| GET    | `/api/recommendations/user/{userId}`         | Get user recommendations     | ✅ Bearer Token |
| GET    | `/api/recommendations/activity/{activityId}` | Get activity recommendations | ✅ Bearer Token |

### Health Check

| Method | Endpoint           | Description               | Auth Required |
| ------ | ------------------ | ------------------------- | ------------- |
| GET    | `/actuator/health` | Application health status | ❌            |

**📖 Full API Documentation:**

- Swagger UI: http://localhost:8080/swagger-ui.html (when running locally)

---

## 🛡️ Security Implementation

### 🔒 Authentication & Authorization

**JWT Token Flow:**

1. User submits credentials to `/api/auth/login`
2. Backend validates credentials using BCrypt password comparison
3. JWT token generated with user ID and role (expiry: 24 hours)
4. Token returned to client, stored in localStorage
5. Client includes token in `Authorization: Bearer <token>` header
6. `JwtAuthenticationFilter` validates token on every request
7. Spring Security grants access based on user role

**Password Security:**

- All passwords hashed using BCrypt (cost factor: 10)
- Password strength validation: min 8 characters
- Never stored or logged in plain text

**Role-Based Access Control:**

- `USER` role: Can manage own activities and profile
- `ADMIN` role: Can manage all users and activities
- Method-level security with `@PreAuthorize("hasRole('ADMIN')")`

### 🔐 SQL Injection Prevention

**JPA Parameterized Queries:**

```java
// ✅ SAFE - Using JPA method names (parameterized internally)
User findByEmail(String email);

// ✅ SAFE - Using @Query with parameters
@Query("SELECT u FROM User u WHERE u.email = :email")
User findUserByEmail(@Param("email") String email);
```

**All database queries use:**

- Spring Data JPA method names
- JPQL with named parameters (`:param`)
- Hibernate PreparedStatement (parameterized queries)
- **Zero** string concatenation for SQL queries

### 🛡️ Input Validation

**Backend Validation (Jakarta Bean Validation):**

```java
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be 8-100 characters")
    private String password;
}
```

**Frontend Validation:**

- Reactive Forms with custom validators
- Real-time validation feedback
- Input sanitization

### 🔒 Environment Variable Security

**What's Protected:**

```bash
# ❌ NEVER IN GIT - backend/.env
DB_URL=jdbc:mysql://localhost:3306/fitness_db
DB_USER=actual_username
DB_PWD=actual_password123!
JWT_SECRET=ActualSecretKey256Bits
```

**What's Safe in Git:**

```bash
# ✅ SAFE - backend/.env.example
DB_URL=jdbc:mysql://localhost:3306/fitness_db
DB_USER=your_db_username
DB_PWD=your_db_password
JWT_SECRET=your-256-bit-secret-key-here
```

**.gitignore Protection:**

```
# Environment files (NOT committed)
.env
.env.local
.env.prod
backend/.env
frontend/.env
```

### 🚨 Additional Security Measures

- ✅ **CORS**: Configured allowed origins (`http://localhost:4200`)
- ✅ **HTTPS**: Ready for SSL/TLS in deployment
- ✅ **Error Handling**: Generic error messages (no sensitive data exposure)
- ✅ **XSS Protection**: Input sanitization and output encoding
- ✅ **CSRF**: Stateless authentication (no cookies by default)
- ✅ **Email Normalization**: Emails trimmed and lowercased
- ✅ **Logging**: No credentials or sensitive data in logs

---

## 💾 Database Configuration

### Local Development (MySQL)

**Configuration in `application-dev.properties`:**

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PWD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Tables Created Automatically by JPA:**

- `users` - User accounts (id, email, password_hash, first_name, last_name, role)
- `activities` - Fitness activities (id, user_id, type, duration, calories, date)
- `recommendations` - AI recommendations (id, user_id, activity_id, recommendation_text)

### Cloud Deployment (Neon DB)

**Neon DB Setup:**

1. Create account at [neon.tech](https://neon.tech/)
2. Create new project and database
3. Get connection string

**Configuration in `application-prod.properties`:**

```properties
# Replace with your Neon DB connection string
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PWD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**Neon DB Connection String Format:**

```
postgresql://[user]:[password]@[endpoint]/[database]?sslmode=require
```

### Future: AWS RDS

**Planned migration to AWS RDS:**

- MySQL 8.0 or PostgreSQL on RDS
- Multi-AZ deployment for high availability
- Automated backups
- Read replicas for scaling

---

## 🚀 Deployment Options

### Option 1: Docker Deployment

**Build and Run with Docker:**

```bash
# Build backend image
cd backend
docker build -t fitness-backend:latest .

# Build frontend image
cd ../frontend
docker build -t fitness-frontend:latest .

# Run containers
docker run -d -p 8080:8080 --env-file backend/.env fitness-backend
docker run -d -p 4200:80 fitness-frontend
```

### Option 2: Cloud Deployment (Neon DB + AWS)

**Step 1: Deploy Database (Neon DB)**

1. Sign up at [neon.tech](https://neon.tech/)
2. Create new project
3. Copy connection string
4. Update `backend/.env` with Neon DB credentials

**Step 2: Deploy Backend (AWS EC2 or Elastic Beanstalk)**

```bash
# Package application
./mvnw clean package -DskipTests

# Deploy JAR to AWS EC2
# Upload fitness-management-system-0.0.1-SNAPSHOT.jar
# Run: java -jar fitness-management-system-0.0.1-SNAPSHOT.jar
```

**Step 3: Deploy Frontend (AWS S3 + CloudFront)**

```bash
# Build for deployment
npm run build

# Upload dist/frontend/* to S3 bucket
# Configure S3 for static website hosting
# Set up CloudFront for CDN
```

### Option 3: Platform-as-a-Service

**Backend Options:**

- Heroku
- Railway
- Render
- AWS Elastic Beanstalk

**Frontend Options:**

- Vercel
- Netlify
- AWS Amplify
- AWS S3 + CloudFront

---

│ │ │ │ └── profile/ # User profile
│ │ │ ├── layouts/ # Layout components
│ │ │ ├── shared/ # Shared components
│ │ │ └── app.routes.ts # Route configuration
│ │ ├── environments/ # Environment configs
│ │ ├── styles.scss # Global styles
│ │ └── index.html # Main HTML
│ ├── .env.example # Environment template
│ ├── package.json # npm dependencies
│ ├── angular.json # Angular configuration
│ ├── tailwind.config.js # Tailwind configuration
│ └── Dockerfile # Docker configuration
│
├── SECURITY.md # Security guidelines
├── SECURITY_AUDIT.md # Security audit report
└── README.md # This file

````

---

## 🔌 API Endpoints

### Authentication

| Method | Endpoint             | Description       | Auth |
| ------ | -------------------- | ----------------- | ---- |
| POST   | `/api/auth/register` | Register new user | ❌   |
| POST   | `/api/auth/login`    | Login user        | ❌   |

### Users

| Method | Endpoint                     | Description              | Auth     |
| ------ | ---------------------------- | ------------------------ | -------- |
| GET    | `/api/users/profile`         | Get current user profile | ✅       |
| PUT    | `/api/users/profile`         | Update user profile      | ✅       |
| POST   | `/api/users/change-password` | Change password          | ✅       |
| GET    | `/api/users/{id}`            | Get user by ID (Admin)   | ✅ Admin |
| GET    | `/api/users`                 | Get all users (Admin)    | ✅ Admin |
| DELETE | `/api/users/{id}`            | Delete user (Admin)      | ✅ Admin |

### Activities

| Method | Endpoint               | Description         | Auth |
| ------ | ---------------------- | ------------------- | ---- |
| POST   | `/api/activities`      | Create activity     | ✅   |
| GET    | `/api/activities`      | Get user activities | ✅   |
| GET    | `/api/activities/{id}` | Get activity by ID  | ✅   |

### Recommendations

| Method | Endpoint                                     | Description                  | Auth |
| ------ | -------------------------------------------- | ---------------------------- | ---- |
| POST   | `/api/recommendations/generate`              | Generate AI recommendation   | ✅   |
| GET    | `/api/recommendations/user/{userId}`         | Get user recommendations     | ✅   |
| GET    | `/api/recommendations/activity/{activityId}` | Get activity recommendations | ✅   |

### Health

| Method | Endpoint           | Description  | Auth |
| ------ | ------------------ | ------------ | ---- |
| GET    | `/actuator/health` | Health check | ❌   |

---

## 🛡️ Security Features

### 🔒 Authentication & Authorization

- **JWT Tokens**: Stateless authentication with HS512 algorithm
- **BCrypt Hashing**: Passwords hashed with BCrypt (cost factor 10)
- **Role-Based Access**: USER and ADMIN roles with method-level security
- **Token Expiration**: 24-hour token validity

### 🔐 Data Protection

- **SQL Injection Prevention**: JPA parameterized queries
- **Input Validation**: Jakarta Bean Validation on all DTOs
- **CORS Protection**: Configured allowed origins
- **XSS Prevention**: Input sanitization and output encoding
- **Exception Handling**: No sensitive data in error responses

### 🚨 Security Best Practices

- ✅ No hardcoded credentials (environment variables only)
- ✅ `.env` files excluded from version control
- ✅ Generic error messages (no information disclosure)
- ✅ HTTP-only cookies (if enabled)
- ✅ Secure password requirements
- ✅ Email normalization (lowercase, trimmed)

**📖 Read full security guidelines:** [SECURITY.md](SECURITY.md)

---

## 🔑 Environment Configuration

### Backend Environment Variables

Create `backend/.env`:

```env
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/fitness_db
DB_USER=your_db_username
DB_PWD=your_db_password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters
JWT_EXPIRATION=86400000

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:4200

# Server Configuration
PORT=8080
````

### Frontend Environment Variables

Create `frontend/.env`:

```env
# API Configuration
API_URL=http://localhost:8080/api
```

**⚠️ IMPORTANT:** Never commit actual `.env` files to version control!

---

## 💾 Database Setup

### Automatic Schema Creation

Spring Boot automatically creates tables on startup using JPA entities:

- `users` - User accounts
- `activities` - Fitness activities
- `recommendations` - AI recommendations

### Manual Schema (Optional)

If you prefer manual control, set in `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Then run migrations manually.

## 📸 Screenshots

### Landing Page

_Modern, responsive landing page with fitness-themed design_

### User Dashboard

_Real-time activity tracking and progress visualization_

### Activity Management

_Easy-to-use interface for logging workouts_

### AI Recommendations

_Personalized fitness suggestions based on activity history_

### Admin Panel

_Comprehensive user and activity management_

---

## 🎯 Future Enhancements

### Planned Features

- [ ] **Mobile Application**: React Native app for iOS and Android
- [ ] **Advanced AI**: Machine learning model for better recommendations
- [ ] **Social Features**: Friend connections and activity sharing
- [ ] **Wearable Integration**: Sync with Fitbit, Apple Watch, Garmin
- [ ] **Nutrition Tracking**: Meal logging and calorie counting
- [ ] **Workout Plans**: Pre-built and custom workout programs
- [ ] **Progress Photos**: Before/after photo tracking
- [ ] **Notifications**: Email/Push notifications for goals and achievements

### Deployment Roadmap

- [x] Local development with MySQL
- [ ] Neon DB integration (Serverless PostgreSQL)
- [ ] AWS deployment (EC2 + RDS + S3)
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Monitoring and logging (CloudWatch)
- [ ] Auto-scaling configuration

---

## 🤝 Contributing

Contributions are welcome! This project follows standard Git workflow.

**Steps to Contribute:**

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Make changes and test thoroughly
4. Commit: `git commit -m "Add new feature"`
5. Push: `git push origin feature/new-feature`
6. Open Pull Request on GitHub

**Code Standards:**

- Follow Java and TypeScript best practices
- Write unit tests for new features
- Update documentation as needed
- **Never commit credentials** or `.env` files

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 📞 Contact

**Piyush Kumar**

- **GitHub**: [@Piyush-Kumar62](https://github.com/Piyush-Kumar62)
- **Repository**: [Fitness-Management-System](https://github.com/Piyush-Kumar62/Fitness-Management-System)
- **Email**: [Your Email] (if you want to add)
- **LinkedIn**: [Your LinkedIn] (if you want to add)

---

## 🙏 Acknowledgments

- **Spring Boot** team for excellent documentation
- **Angular** team for the modern framework
- **Tailwind CSS** for utility-first styling
- **Neon** for serverless PostgreSQL
- Open source community for various libraries and tools

---

## 📚 Resources & Documentation

### Learning Resources

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Angular Official Docs](https://angular.io/docs)
- [Spring Security with JWT](https://spring.io/guides/topicals/spring-security-architecture)
- [JPA & Hibernate Guide](https://spring.io/guides/gs/accessing-data-jpa/)
- [Angular Reactive Forms](https://angular.io/guide/reactive-forms)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)

### Project Documentation

- **Security Guidelines**: See [SECURITY.md](SECURITY.md)
- **Security Audit Report**: See [SECURITY_AUDIT.md](SECURITY_AUDIT.md)
- **API Documentation**: http://localhost:8080/swagger-ui.html (when running)

---

## ⚠️ Important Notes

### For Developers

- ✅ All database credentials are in `.env` files (NOT in Git)
- ✅ JWT tokens expire after 24 hours
- ✅ Passwords are hashed using BCrypt (never store plain text)
- ✅ All queries use JPA parameterization (SQL injection safe)
- ✅ CORS is configured for `http://localhost:4200`
- ✅ `.gitignore` protects `.env` files from being committed

### For Recruiters

This project demonstrates:

- ✅ Full-stack web development skills
- ✅ RESTful API design and implementation
- ✅ Modern frontend framework (Angular 20)
- ✅ Database design and ORM usage
- ✅ Security best practices
- ✅ Clean code and architecture
- ✅ Git version control
- ✅ Cloud deployment readiness

---

<div align="center">

## ⭐ Star this repository if you find it helpful!

**Built with ❤️ using Spring Boot 3.3.5 & Angular 20**

### 🔗 Quick Links

[View on GitHub](https://github.com/Piyush-Kumar62/Fitness-Management-System) •
[Report Bug](https://github.com/Piyush-Kumar62/Fitness-Management-System/issues) •
[Request Feature](https://github.com/Piyush-Kumar62/Fitness-Management-System/issues)

---

**Last Updated**: January 28, 2026

[⬆ Back to Top](#-fitness-management-system)

</div>

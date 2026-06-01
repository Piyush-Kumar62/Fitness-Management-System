# 🏋️ Fitness Management System

A comprehensive full-stack fitness tracking application with AI-powered recommendations, OAuth2 social authentication (Google & GitHub), and modern role-based access control. Built with Spring Boot 3.5, Java 21, and Angular 20.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Authentication](#-authentication)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Docker Deployment](#-docker-deployment)
- [Deployment Guide](#-deployment-guide)
- [Security](#-security)
- [API Documentation](#-api-documentation)
- [Troubleshooting](#-troubleshooting)

## ✨ Features

### 🔐 Authentication & Authorization

- **Traditional Authentication**: Email/password login with JWT tokens
- **OAuth2 Social Login**: Seamless login with Google and GitHub accounts
- **Role-Based Access Control**: MEMBER, TRAINER, OWNER, and ADMIN roles with protected routes
- **Secure Sessions**: Stateless JWT authentication

### 👤 User Features

- **Activity Tracking**: Log and monitor workout activities (running, cycling, swimming, etc.)
- **Health Metrics**: Track body measurements, BMI, and fitness goals
- **AI Recommendations**: Get personalized workout suggestions based on activity history
- **Progress Milestones**: Set and achieve fitness goals with milestone tracking
- **Profile Management**: Customize user profile with OAuth2 profile picture support

### 👨‍💼 Admin Features

- **User Management**: Full CRUD operations on user accounts
- **Activity Monitoring**: View and manage all system activities
- **Analytics Dashboard**: Comprehensive system metrics and insights
- **File Management**: Upload and manage fitness-related documents

### 🎨 User Experience

- **Responsive Design**: Works seamlessly on mobile, tablet, and desktop
- **Dark Mode**: System-wide theme support with persistent preferences
- **Real-time Notifications**: Toast notifications for user feedback
- **Modern UI**: Built with Tailwind CSS and Angular Material design principles

## 🛠️ Tech Stack

### Backend

- **Framework**: Spring Boot 3.5
- **Language**: Java 21
- **Security**: Spring Security 6.x with OAuth2 support
- **Database**: MySQL 8.0+ (production) / PostgreSQL 15+ (Docker)
- **Authentication**: JWT (JSON Web Tokens) + OAuth2
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Build Tool**: Maven 3.8+

### Frontend

- **Framework**: Angular 20.3
- **Language**: TypeScript 5.9
- **Styling**: Tailwind CSS 3.4
- **State Management**: Angular Signals
- **HTTP Client**: Angular HttpClient with RxJS
- **Build Tool**: Angular CLI

### DevOps

- **Containerization**: Docker & Docker Compose
- **Web Server**: Nginx (frontend), Embedded Tomcat (backend)
- **CI/CD Ready**: GitHub Actions compatible

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java**: JDK 21 or higher ([Download](https://adoptium.net/))
- **Node.js**: 20.x or higher ([Download](https://nodejs.org/))
- **MySQL**: 8.0 or higher ([Download](https://dev.mysql.com/downloads/))
- **Maven**: 3.8+ (or use included wrapper `./mvnw`)
- **Git**: For cloning the repository

Optional:

- **Docker**: For containerized deployment ([Download](https://www.docker.com/))
- **VS Code**: Recommended IDE with Angular and Java extensions

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/fitness-management-system.git
cd fitness-management-system
```

### 2. Set Up MySQL Database

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Exit MySQL
exit;
```

### 3. Configure Backend Environment

```bash
cd backend

# Create .env file (copy from example)
# On Windows:
copy .env.example .env

# On macOS/Linux:
cp .env.example .env
```

Edit `backend/.env` with your credentials:

```env
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/fitness_db?useSSL=false&serverTimezone=UTC
DB_USER=root
DB_PWD=your_mysql_password

# JWT Configuration (32+ chars, raw or base64)
JWT_SECRET=your_super_secret_jwt_key_at_least_32_chars_for_production
JWT_EXPIRATION=86400000

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:4200

# OAuth2 Redirect URI
OAUTH2_REDIRECT_URI=http://localhost:4200/oauth2/redirect

# Google OAuth2 (Optional - for social login)
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# GitHub OAuth2 (Optional - for social login)
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

### 4. Start Backend Server

**Option A: Using Batch File (Windows)**

```bash
# Double-click START_BACKEND_PERSISTENT.bat in the root folder
# Or run from terminal:
START_BACKEND_PERSISTENT.bat
```

**Option B: Manual Start**

```bash
cd backend
./mvnw spring-boot:run
# On Windows: mvnw.cmd spring-boot:run
```

Backend will start on: `http://localhost:8080`

### 5. Start Frontend Application

```bash
# Open new terminal window
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm start
```

Frontend will start on: `http://localhost:4200`

### 6. Access the Application

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Default Test Credentials

After first run, you can create an account or use these test credentials:

```
Email: test@example.com
Password: Test@123
```

## 🔐 Authentication

### Traditional Login/Registration

The application supports standard email/password authentication:

1. Navigate to: http://localhost:4200/auth/register
2. Fill in your details (email, password, first name, last name)
3. Click "Register" - password must be at least 8 characters with uppercase, lowercase, and number
4. Login with your credentials at: http://localhost:4200/auth/login

### OAuth2 Social Login

#### Setting Up Google OAuth2

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Navigate to "APIs & Services" > "Credentials"
4. Click "Create Credentials" > "OAuth 2.0 Client ID"
5. Configure OAuth consent screen if prompted
6. Set Application type: "Web application"
7. Add Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
8. Copy Client ID and Client Secret to `backend/.env`

#### Setting Up GitHub OAuth2

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click "New OAuth App"
3. Fill in details:
   - Application name: "Fitness Management System"
   - Homepage URL: `http://localhost:4200`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Register application and copy Client ID
5. Generate a new client secret
6. Copy both to `backend/.env`

#### Using OAuth2 Login

1. Navigate to: http://localhost:4200/auth/login
2. Click "Sign in with Google" or "Sign in with GitHub"
3. Authorize the application
4. You'll be automatically logged in and redirected to your dashboard

**Note**: OAuth2 users don't need passwords - their accounts are managed by the OAuth provider.

## 📁 Project Structure

```
fitness-management-system/
├── backend/                           # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/project/fitness/
│   │   │   │   ├── config/           # Security, CORS, JWT configuration
│   │   │   │   ├── controller/       # REST API endpoints
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── entity/           # JPA entities (User, Activity, etc.)
│   │   │   │   ├── exception/        # Global exception handling
│   │   │   │   ├── repository/       # JPA repositories
│   │   │   │   ├── security/         # JWT & OAuth2 security classes
│   │   │   │   └── service/          # Business logic
│   │   │   └── resources/
│   │   │       ├── application.properties       # Main config
│   │   │       ├── application-dev.properties   # Dev config
│   │   │       └── application-prod.properties  # Production config
│   │   └── test/                     # Unit and integration tests
│   ├── .env.example                  # Environment template
│   ├── Dockerfile                    # Backend container
│   └── pom.xml                       # Maven dependencies
│
├── frontend/                          # Angular SPA
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/                 # Core services (auth, guards, interceptors)
│   │   │   ├── features/             # Feature modules
│   │   │   │   ├── admin/           # Admin dashboard
│   │   │   │   ├── auth/            # Login, register, OAuth2
│   │   │   │   ├── landing/         # Landing page
│   │   │   │   └── user/            # User dashboard & features
│   │   │   ├── layouts/             # Layout components
│   │   │   └── shared/              # Shared components
│   │   ├── environments/            # Environment configurations
│   │   └── index.html               # Main HTML file
│   ├── Dockerfile                   # Frontend container
│   ├── nginx.conf                   # Nginx configuration
│   └── package.json                 # NPM dependencies
│
├── docker-compose.yml               # Multi-container orchestration
├── START_BACKEND_PERSISTENT.bat     # Windows backend launcher
├── START_FRONTEND.bat               # Windows frontend launcher
├── SECURITY.md                      # Security guidelines
└── README.md                        # This file
```

## ⚙️ Configuration

### Backend Configuration

The backend uses Spring profiles for different environments:

- **application.properties**: Base configuration
- **application-dev.properties**: Development settings (active by default)
- **application-prod.properties**: Production settings

Key configuration properties:

```properties
# Active profile
spring.profiles.active=dev

# Server port
server.port=8080

# Database (uses environment variables)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PWD}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# OAuth2
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
```

### Frontend Configuration

Edit `frontend/src/environments/environment.ts` for development:

```typescript
export const environment = {
  production: false,
  apiUrl: "http://localhost:8080/api",
};
```

For production (`environment.prod.ts`):

```typescript
export const environment = {
  production: true,
  apiUrl: "https://your-api-domain.com/api",
};
```

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

The project includes a complete Docker Compose setup with PostgreSQL database:

```bash
# Build all containers
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

Access the application:

- **Frontend**: http://localhost:80
- **Backend**: http://localhost:8080
- **PostgreSQL**: localhost:5432

### Individual Container Build

**Backend**:

```bash
cd backend
docker build -t fitness-backend .
docker run -p 8080:8080 --env-file .env fitness-backend
```

**Frontend**:

```bash
cd frontend
docker build -t fitness-frontend .
docker run -p 80:80 fitness-frontend
```

## 🚀 Deployment Guide

For production-style setup with **Render + Neon + Vercel**, CI-gated auto-deploy, and keepalive bot:

- See [DEPLOYMENT.md](DEPLOYMENT.md)

## 🛡️ Security

### Security Features Implemented

✅ **Password Security**

- BCrypt hashing with configurable strength
- Password validation (min 8 chars, uppercase, lowercase, number)
- Passwords are optional for OAuth2 users

✅ **Authentication & Authorization**

- JWT token-based stateless authentication
- Role-based access control (MEMBER, TRAINER, OWNER, ADMIN)
- OAuth2 integration with Google and GitHub

✅ **API Security**

- CORS protection with configurable allowed origins
- CSRF protection for state-changing operations
- Request validation with Jakarta Validation
- Global exception handling with sanitized responses

✅ **Data Protection**

- SQL injection prevention (JPA/Hibernate parameterized queries)
- Input validation and sanitization
- XSS protection
- Sensitive data stored in environment variables

✅ **Database Security**

- Connection pooling with HikariCP
- Prepared statements for all queries
- Database credentials via environment variables

### Security Best Practices

⚠️ **IMPORTANT: Before Deploying to Production**

1. **Environment Variables**: Never commit `.env` files
2. **Strong Secrets**: Use strong, random JWT secrets (256+ bits)
3. **HTTPS Only**: Enable SSL/TLS for production
4. **Rate Limiting**: Implement rate limiting on auth endpoints
5. **Regular Updates**: Keep dependencies updated for security patches
6. **Monitoring**: Enable logging and monitoring for suspicious activities
7. **Backups**: Schedule regular database backups

For detailed security guidelines, see [SECURITY.md](SECURITY.md).

## 📚 API Documentation

### Swagger UI

Interactive API documentation is available at:

- **Development**: http://localhost:8080/swagger-ui.html
- **Docs JSON**: http://localhost:8080/v3/api-docs

### Key Endpoints

#### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login with credentials
- `GET /oauth2/authorization/google` - Initiate Google login
- `GET /oauth2/authorization/github` - Initiate GitHub login

#### User Management

- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users` - List all users (ADMIN only)

#### Activities

- `GET /api/activities` - Get user's activities
- `POST /api/activities` - Create new activity
- `PUT /api/activities/{id}` - Update activity
- `DELETE /api/activities/{id}` - Delete activity

#### Health & Monitoring

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information

For complete API documentation, refer to Swagger UI.

## 🔧 Troubleshooting

### Backend Won't Start

**Problem**: Backend shuts down immediately after startup  
**Solution**: Ensure no code runs after `SpringApplication.run()` in the main method

**Problem**: Database connection fails  
**Solution**:

- Verify MySQL is running: `mysql -u root -p`
- Check database exists: `SHOW DATABASES;`
- Verify credentials in `backend/.env`

### OAuth2 Authentication Issues

#### 🔴 Google OAuth2 Not Working

**Problem**: "redirect_uri_mismatch" error  
**Solution**:

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to: APIs & Services → Credentials
3. Click on your OAuth 2.0 Client ID
4. Under "Authorized redirect URIs", ensure **EXACT** match:
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
5. Remove any trailing slashes or extra spaces
6. Save and wait 1-2 minutes for changes to propagate

**Problem**: "Access blocked: Fitness Management System has not completed the Google verification process"  
**Solution**:

1. Go to OAuth consent screen in Google Cloud Console
2. Add your test Gmail account to "Test users"
3. During development, keep app in "Testing" mode
4. For production, complete Google's verification process

**Problem**: "Error 400: invalid_client"  
**Solution**:

- Verify `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` in `backend/.env` match Google Console
- Ensure there are no extra spaces or quotes around values
- Restart backend server after changing `.env`

#### ⚫ GitHub OAuth2 Not Working

**Problem**: "redirect_uri_mismatch" or "Redirect URI mismatch"  
**Solution**:

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click on your OAuth App
3. Verify "Authorization callback URL" is **EXACTLY**:
   ```
   http://localhost:8080/login/oauth2/code/github
   ```
4. Update and save

**Problem**: "Application suspended" or "This application has been suspended"  
**Solution**:

- Create a new OAuth App in GitHub
- Replace `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` in `.env`
- Restart backend

**Problem**: "bad_verification_code"  
**Solution**:

- This usually means the OAuth flow was interrupted
- Clear browser cookies for `localhost:8080` and `github.com`
- Try the OAuth flow again
- Check backend logs for detailed error messages

#### 🔍 General OAuth2 Debugging

**Problem**: "Authentication failed" after clicking Google/GitHub button  
**Solution**:

1. **Check backend logs** in terminal for exact error message
2. **Verify redirect URI format**:
   - Backend expects: `http://localhost:4200/oauth2/redirect`
   - Set this in `backend/.env` as `OAUTH2_REDIRECT_URI`
3. **Check database columns exist**:
   ```sql
   USE fitness_db;
   DESCRIBE users;
   -- Should show: provider, provider_id, profile_image_url columns
   ```
4. **Test OAuth2 URLs directly**:
   - Google: `http://localhost:8080/oauth2/authorization/google`
   - GitHub: `http://localhost:8080/oauth2/authorization/github`
   - Should redirect to provider login page

**Problem**: OAuth2 login succeeds but user not created in database  
**Solution**:

1. Check `CustomOAuth2UserService` is processing user correctly
2. Verify email is being extracted from OAuth2 attributes
3. Check database logs for constraint violations
4. Ensure `users` table has nullable `password` column for OAuth2 users

**Problem**: Frontend shows "Token not found" after OAuth2 redirect  
**Solution**:

1. Verify `OAuth2AuthenticationSuccessHandler` is generating JWT token
2. Check frontend `OAuth2RedirectComponent` extracts token from URL parameter
3. Test redirect URL format: `http://localhost:4200/oauth2/redirect?token=eyJ...`
4. Open browser DevTools → Console to see any JavaScript errors

#### ✅ How to Test OAuth2 is Working

1. **Check backend is ready**:

   ```bash
   curl http://localhost:8080/actuator/health
   # Should return: {"status":"UP"}
   ```

2. **Test OAuth2 endpoints**:
   - Open: `http://localhost:8080/oauth2/authorization/google`
   - Should redirect to Google login page (not show error)

3. **Complete OAuth2 flow**:
   - Click "Sign in with Google" in your app
   - Login and authorize
   - Should redirect to: `http://localhost:4200/oauth2/redirect?token=...`
   - Should automatically navigate to dashboard

4. **Verify database**:
   ```sql
   USE fitness_db;
   SELECT email, first_name, provider, profile_image_url
   FROM users
   WHERE provider IN ('google', 'github');
   ```

#### 📋 OAuth2 Setup Checklist

**Google OAuth2**:

- [ ] Created project in Google Cloud Console
- [ ] Enabled Google+ API
- [ ] Created OAuth 2.0 Client ID (Web application)
- [ ] Added redirect URI: `http://localhost:8080/login/oauth2/code/google`
- [ ] Added test users to OAuth consent screen
- [ ] Copied Client ID and Secret to `backend/.env`
- [ ] Restarted backend server

**GitHub OAuth2**:

- [ ] Created OAuth App in GitHub Settings
- [ ] Set Homepage URL: `http://localhost:4200`
- [ ] Set Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
- [ ] Copied Client ID and Secret to `backend/.env`
- [ ] Restarted backend server

**Backend Configuration**:

- [ ] All OAuth2 environment variables set in `.env`
- [ ] `OAUTH2_REDIRECT_URI` points to frontend: `http://localhost:4200/oauth2/redirect`
- [ ] Database has OAuth2 columns (provider, provider_id, profile_image_url)
- [ ] Backend server restarted after config changes

**Frontend Configuration**:

- [ ] OAuth2 redirect route configured: `/oauth2/redirect`
- [ ] `OAuth2RedirectComponent` extracts and stores token
- [ ] Login page has Google and GitHub buttons
- [ ] Buttons call: `/oauth2/authorization/google` and `/oauth2/authorization/github`

### Frontend Issues

**Problem**: Cannot connect to backend API  
**Solution**:

- Verify backend is running on port 8080
- Check CORS configuration in `application-dev.properties`
- Confirm `apiUrl` in `environment.ts` is correct

**Problem**: "CORS policy" error in browser console  
**Solution**:

- Add `http://localhost:4200` to `ALLOWED_ORIGINS` in `backend/.env`
- Verify `spring.security.cors.allowed-origins=${ALLOWED_ORIGINS}` in properties
- Restart backend server

### Database Issues

**Problem**: Tables not created automatically  
**Solution**:

- Check `spring.jpa.hibernate.ddl-auto=update` in properties
- Manually run SQL scripts from `backend/database_*.sql`
- Verify database user has CREATE permissions

**Problem**: OAuth2 users can't log in  
**Solution**:

- Run `backend/database_oauth2_setup.sql` to add OAuth2 columns
- Verify email is being captured correctly from OAuth2 provider
- Check logs for detailed error messages

## 📝 Additional Resources

- **Frontend Details**: See [frontend/README.md](frontend/README.md) for Angular-specific documentation
- **Security Guidelines**: Review [SECURITY.md](SECURITY.md) for security best practices
- **Docker Compose**: Check [docker-compose.yml](docker-compose.yml) for container configuration

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

**Important**: Never commit sensitive information like passwords, API keys, or `.env` files.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Built with Spring Boot and Angular frameworks
- OAuth2 integration powered by Spring Security
- UI components styled with Tailwind CSS
- Database: MySQL & PostgreSQL support

---

**Built with ❤️ for fitness enthusiasts everywhere**

Need help? Open an issue on GitHub or contact the maintainers.

# 🏋️ Fitness Management System

A full-stack fitness tracking application with AI-powered recommendations, built with Spring Boot and Angular.

## 🔒 Security Notice

**⚠️ IMPORTANT: Before pushing to GitHub**

This project uses environment variables for all sensitive credentials. Make sure you:

1. **Never commit** `.env` files to version control
2. **Create your own** `.env` file using `.env.example` as a template
3. **Use strong passwords** and secrets in production
4. **Review** [SECURITY.md](SECURITY.md) for detailed security guidelines

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### Monorepo Setup (Recommended)

```bash
# Install dependencies for both backend and frontend
npm run install:all

# Build both applications
npm run build:all

# Start both in development mode (runs concurrently)
npm run dev
```

Access the application at `http://localhost:4200`

### Available Scripts

From the root directory, you can run:

- `npm run install:all` - Install dependencies for both projects
- `npm run build:all` - Build both backend and frontend
- `npm run dev` - Start both applications in development mode
- `npm run test:all` - Run tests for both projects
- `npm run docker:build` - Build Docker containers
- `npm run docker:up` - Start all services with Docker Compose
- `npm run docker:down` - Stop Docker services

### Individual Setup

#### Backend Setup

```bash
cd backend
# Copy and configure environment variables
cp .env.example .env
# Edit .env with your actual credentials

# Run the application
./mvnw spring-boot:run
```

#### Frontend Setup

```bash
cd frontend
npm install
npm start
```

## 📁 Project Structure

This is a monorepo containing both backend and frontend applications:

```
fitness-management-system/     # Monorepo root
├── backend/                  # Spring Boot REST API
│   ├── src/main/java/        # Java source code
│   ├── src/main/resources/   # Application properties
│   ├── src/test/             # Unit tests
│   ├── .env.example         # Environment template
│   ├── Dockerfile           # Backend container
│   └── pom.xml              # Maven configuration
├── frontend/                 # Angular SPA
│   ├── src/                 # Angular source code
│   ├── public/              # Static assets
│   ├── Dockerfile           # Frontend container
│   └── package.json         # NPM configuration
├── docker-compose.yml       # Multi-container setup
├── cleanup.sh              # Cleanup script
├── package.json            # Root scripts for monorepo
├── .gitignore              # Git ignore rules
├── .gitattributes          # Git attributes
└── README.md               # This file
```

## 🛡️ Security Features

- ✅ BCrypt password hashing
- ✅ JWT token authentication
- ✅ Input validation & sanitization
- ✅ SQL injection prevention (JPA/Hibernate)
- ✅ CORS protection
- ✅ Role-based access control
- ✅ Environment variable configuration
- ✅ Secure exception handling

## 🔑 Environment Variables

All sensitive data is managed through environment variables. See:

- Backend: `backend/.env.example`
- Frontend: `frontend/.env.example`

**Never commit actual `.env` files!**

## 📚 Documentation

- [Backend Documentation](backend/README.md)
- [Frontend Documentation](frontend/README.md)
- [Security Guidelines](SECURITY.md)
- [API Documentation](backend/API.md)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Never commit credentials
4. Submit a pull request

## ⚠️ Important Notes

- All passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- Database credentials are in `.env` (not tracked by git)
- CORS is configured for `http://localhost:4200`

## 📄 License

This project is licensed under the MIT License.

---

**Built with ❤️ using Spring Boot & Angular**

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

### Backend Setup

```bash
cd backend
# Copy and configure environment variables
cp .env.example .env
# Edit .env with your actual credentials

# Run the application
./mvnw spring-boot:run
```

### Frontend Setup

```bash
cd frontend
npm install
npm start
```

Access the application at `http://localhost:4200`

## 📁 Project Structure

```
fitness-management-system/
├── backend/                 # Spring Boot application
│   ├── src/
│   ├── .env.example        # Environment variables template
│   └── pom.xml
├── frontend/               # Angular application
│   ├── src/
│   └── package.json
├── SECURITY.md            # Security guidelines
└── README.md
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

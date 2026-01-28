<div align="center">

# 🏋️ Fitness Management System - Frontend

[![Angular](https://img.shields.io/badge/Angular-20-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.5-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind-3.4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

**A professionally architected Angular 20 frontend application for fitness activity tracking with AI-powered recommendations**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Quick Start](#-quick-start) • [API Integration](#-api-integration)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Running the Application](#-running-the-application)
- [Docker Deployment](#-docker-deployment)
- [API Integration](#-api-integration)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Testing](#-testing)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)

---

## 🎯 Overview

The **Fitness Management System Frontend** is a comprehensive Angular 20 application built with standalone components, TypeScript strict mode, and Tailwind CSS. It provides a complete user interface for fitness tracking, activity management, and AI-powered recommendations.

### Why This Project?

- ✅ **Modern Architecture**: Angular 20 with standalone components
- ✅ **Type-Safe**: TypeScript 5.5+ with strict mode enabled
- ✅ **Responsive Design**: Mobile-first approach with Tailwind CSS
- ✅ **Deployment-Ready**: Docker containerization with Nginx
- ✅ **Best Practices**: Signal-based state management, reactive forms
- ✅ **Well-Documented**: Comprehensive documentation and code comments

---

## ✨ Features

### 🔐 Authentication & Authorization

- JWT-based authentication with token refresh
- Role-based access control (USER / ADMIN)
- Auto-login with persistent sessions
- Protected routes with auth guards

### 👤 User Features

- **Dashboard**: Activity overview with statistics
- **Activity Tracking**: CRUD operations for workout logging
- **AI Recommendations**: Personalized fitness suggestions
- **BMI Calculator**: Health metrics calculator
- **Profile Management**: User profile and settings
- **Dark Mode**: System-wide theme toggle

### 👨‍💼 Admin Features

- **User Management**: CRUD with pagination and search
- **Activity Monitoring**: View all system activities
- **Analytics Dashboard**: System metrics and insights
- **Role Management**: Manage user permissions

### 🎨 UI/UX Features

- **Responsive Design**: Works on mobile, tablet, and desktop
- **Dark Mode**: Persistent theme switching
- **Toast Notifications**: Real-time user feedback
- **Loading States**: Global loading indicator
- **Error Handling**: User-friendly error messages
- **404 Page**: Custom not found page

---

## 🛠️ Tech Stack

| Technology          | Version | Purpose               |
| ------------------- | ------- | --------------------- |
| **Angular**         | 20.3.16 | Frontend framework    |
| **TypeScript**      | 5.5+    | Type-safe JavaScript  |
| **Tailwind CSS**    | 3.4     | Utility-first CSS     |
| **RxJS**            | 7.8     | Reactive programming  |
| **Angular Signals** | ✓       | State management      |
| **Docker**          | ✓       | Containerization      |
| **Nginx**           | 1.25    | Deployment web server |

---

## 🏗️ Architecture

### Component Structure

```
src/app/
├── core/                           # Core application logic
│   ├── guards/                    # Route guards (auth, role, guest)
│   │   ├── auth.guard.ts
│   │   ├── role.guard.ts
│   │   └── guest.guard.ts
│   ├── interceptors/              # HTTP interceptors
│   │   ├── auth.interceptor.ts
│   │   ├── error.interceptor.ts
│   │   └── loading.interceptor.ts
│   ├── models/                    # TypeScript interfaces
│   │   ├── user.model.ts
│   │   ├── activity.model.ts
│   │   └── recommendation.model.ts
│   └── services/                  # Business logic services
│       ├── auth.service.ts
│       ├── user.service.ts
│       ├── activity.service.ts
│       └── recommendation.service.ts
├── features/                       # Feature modules
│   ├── auth/                      # Authentication
│   │   ├── login/
│   │   ├── register/
│   │   └── forgot-password/
│   ├── user/                      # User features
│   │   ├── dashboard/
│   │   ├── activities/
│   │   ├── recommendations/
│   │   ├── bmi-calculator/
│   │   ├── profile/
│   │   └── settings/
│   └── admin/                     # Admin features
│       ├── admin-dashboard/
│       ├── users/
│       ├── activities/
│       └── analytics/
├── shared/                         # Shared components
│   ├── components/
│   │   ├── header/
│   │   ├── sidebar/
│   │   ├── toast/
│   │   ├── loading-spinner/
│   │   └── not-found/
│   └── pipes/
└── layouts/                        # Layout wrappers
    ├── auth-layout/
    └── main-layout/
```

---

## 📋 Prerequisites

### Required

- **Node.js** v20.x or higher - [Download](https://nodejs.org/)
- **npm** v10.x or higher
- **Backend API** running on port 8080 - [Backend Repo](https://github.com/Piyush-Kumar62/Fitness-Management-System)

### Optional

- **Angular CLI** v20.x - `npm install -g @angular/cli`
- **Docker** & **Docker Compose**

---

## 📥 Installation

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/fitness-frontend.git
cd fitness-frontend

# Install dependencies
npm install

# Configure environment (optional)
# Update src/environments/environment.ts if backend is not on localhost:8080
```

---

## 🚀 Running the Application

### Development Mode

```bash
# Start development server
npm start

# Or with Angular CLI
ng serve

# Application will open at http://localhost:4200
```

### Optimized Build

```bash
# Build for deployment
npm run build:prod

# Output will be in dist/frontend/browser
```

### With Backend Integration

```bash
# Terminal 1 - Start backend
cd ../Fitness-Management-System
./mvnw spring-boot:run

# Terminal 2 - Start frontend
cd ../frontend
npm start
```

---

## 🐳 Docker Deployment

### Using Docker Compose (Full Stack)

```bash
# Create environment file
cp .env.example .env

# Edit .env with your configuration
# Set DB_PASSWORD and JWT_SECRET

# Build and start all services
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f frontend

# Stop services
docker-compose down
```

### Using Docker Only (Frontend)

```bash
# Build Docker image
docker build -t fitness-frontend:latest .

# Run container
docker run -d -p 80:80 fitness-frontend:latest

# Access at http://localhost:80
```

---

## 📡 API Integration

### Backend Requirements

The frontend expects the backend API to be running with the following endpoints:

#### Authentication

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh

#### User Management

- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile
- `GET /api/users` - List all users (admin)
- `POST /api/users` - Create user (admin)
- `DELETE /api/users/{id}` - Delete user (admin)

#### Activities

- `GET /api/activities` - List activities
- `POST /api/activities` - Create activity
- `PUT /api/activities/{id}` - Update activity
- `DELETE /api/activities/{id}` - Delete activity

#### Recommendations

- `GET /api/recommendations` - List recommendations
- `POST /api/recommendations/generate` - Generate recommendation

### Environment Configuration

Update `src/environments/environment.ts` for development:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api', // Change this to your backend URL
};
```

For production (`src/environments/environment.prod.ts`):

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-api-domain.com/api',
};
```

---

## 📁 Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                      # Core services, guards, models
│   │   ├── features/                  # Feature modules
│   │   ├── shared/                    # Shared components
│   │   ├── layouts/                   # Layout wrappers
│   │   ├── app.component.ts          # Root component
│   │   ├── app.config.ts             # App configuration
│   │   └── app.routes.ts             # Routing configuration
│   ├── assets/                        # Static assets
│   ├── environments/                  # Environment configs
│   ├── styles.scss                    # Global styles
│   └── index.html                     # HTML entry point
├── scripts/                           # Build & deployment scripts
│   ├── build.sh
│   ├── deploy.sh
│   └── dev-start.sh
├── .github/workflows/                 # CI/CD pipelines
│   └── ci-cd.yml
├── docker-compose.yml                 # Docker Compose config
├── Dockerfile                         # Docker build config
├── nginx.conf                         # Nginx configuration
├── proxy.conf.json                    # Development proxy
├── tailwind.config.js                 # Tailwind configuration
├── tsconfig.json                      # TypeScript configuration
├── angular.json                       # Angular CLI configuration
└── package.json                       # Dependencies & scripts
```

---

## ⚙️ Configuration

### Proxy Configuration (Development)

`proxy.conf.json` is used to proxy API requests during development:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

### Tailwind Configuration

Custom theme colors and animations in `tailwind.config.js`:

```javascript
theme: {
  extend: {
    colors: {
      primary: {
        500: '#6366f1',
        600: '#4f46e5',
        // ...
      },
    },
  },
}
```

### Angular Configuration

Key settings in `angular.json`:

- Output path: `dist/frontend`
- Styles: `src/styles.scss`
- Proxy config: `proxy.conf.json`

---

## 🧪 Testing

```bash
# Run unit tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --code-coverage

# View coverage report
# Open coverage/index.html in browser
```

---

## 🐛 Troubleshooting

### Common Issues

#### Issue: CORS Errors

**Solution:**

1. Check backend CORS configuration
2. Verify `ALLOWED_ORIGINS` includes `http://localhost:4200`
3. Restart backend after changing CORS settings

#### Issue: API Connection Refused

**Solution:**

1. Verify backend is running on port 8080
2. Check `environment.ts` apiUrl setting
3. Check `proxy.conf.json` target URL

#### Issue: npm install fails

**Solution:**

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

#### Issue: Docker build fails

**Solution:**

```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

#### Issue: Angular compilation errors

**Solution:**

```bash
# Delete .angular cache
rm -rf .angular

# Restart development server
npm start
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'feat: add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Piyush Kumar**

- 💼 LinkedIn: [linkedin.com/in/piyush-kumar62](https://www.linkedin.com/in/piyush-kumar62/)
- 🐱 GitHub: [github.com/Piyush-Kumar62](https://github.com/Piyush-Kumar62)
- 📧 Email: [piyushkumar30066@gmail.com](mailto:piyushkumar30066@gmail.com)

---

## 🙏 Acknowledgments

- [Angular Team](https://angular.io/) - Amazing frontend framework
- [Tailwind CSS](https://tailwindcss.com/) - Utility-first CSS framework
- [RxJS](https://rxjs.dev/) - Reactive programming library

---

## 📊 Project Stats

- **Total Components**: 35+
- **Total Services**: 15+
- **Total Lines of Code**: 15,000+
- **Test Coverage**: Target 80%+

---

## 🗺️ Feature Roadmap

### v1.0.0 (Current)

- ✅ Complete authentication system
- ✅ User dashboard with activity tracking
- ✅ Admin panel with user management
- ✅ Dark mode support
- ✅ Responsive design
- ✅ Docker deployment

### v1.1.0 (Planned)

- [ ] Real-time notifications (WebSocket)
- [ ] Advanced charts and analytics
- [ ] Export data (CSV, PDF)
- [ ] Social features (follow users)
- [ ] Activity sharing

### v2.0.0 (Future)

- [ ] Mobile app (Ionic/Capacitor)
- [ ] Offline support (PWA)
- [ ] Multi-language support (i18n)
- [ ] Wearable device integration

---

<div align="center">

**Built with ❤️ using Angular 20 and Tailwind CSS**

**⭐ Star this repo if you find it helpful!**

[Report Bug](https://github.com/YOUR_USERNAME/fitness-frontend/issues) · [Request Feature](https://github.com/YOUR_USERNAME/fitness-frontend/issues)

</div>

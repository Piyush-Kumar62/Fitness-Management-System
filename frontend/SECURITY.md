# Security Policy

## Supported Versions

We release patches for security vulnerabilities for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

We take the security of the Fitness Management System seriously. If you believe you have found a security vulnerability, please report it to us as described below.

### Where to Report

**Please DO NOT report security vulnerabilities through public GitHub issues.**

Instead, please email: **[piyushkumar30066@gmail.com](mailto:piyushkumar30066@gmail.com)**

### What to Include

To help us better understand the nature and scope of the issue, please include as much information as possible:

1. **Type of Issue**: Buffer overflow, SQL injection, cross-site scripting, etc.
2. **Full Path**: Source file(s) related to the manifestation of the issue
3. **Location**: Tag/branch/commit or direct URL of affected source code
4. **Configuration**: Any special configuration required to reproduce the issue
5. **Step-by-Step Instructions**: How to reproduce the issue
6. **Proof-of-Concept**: Or exploit code (if possible)
7. **Impact**: What an attacker might be able to achieve
8. **Suggested Fix**: If you have one

### Response Timeline

- **Acknowledgment**: Within 48 hours
- **Initial Assessment**: Within 7 days
- **Status Update**: Weekly until resolved
- **Fix Timeline**: Varies based on severity
  - **Critical**: Within 7 days
  - **High**: Within 14 days
  - **Medium**: Within 30 days
  - **Low**: Within 60 days

### Disclosure Policy

- We will acknowledge receipt of your vulnerability report
- We will confirm the vulnerability and determine its impact
- We will release a fix as per the timeline above
- We will publicly disclose the vulnerability after a fix is released

### Comments on This Policy

If you have suggestions on how this process could be improved, please submit a pull request.

---

## Security Best Practices

### For Users

1. **Strong Passwords**: Use passwords with at least 8 characters including uppercase, lowercase, numbers, and special characters
2. **Keep Updated**: Always use the latest version of the application
3. **Secure Environment**: Don't share your JWT tokens or credentials
4. **Logout**: Always logout after using the application, especially on shared devices
5. **HTTPS**: Always access the application over HTTPS in deployment environments

### For Developers

1. **Environment Variables**: Never commit secrets or API keys to version control
2. **Dependencies**: Regularly update dependencies to patch known vulnerabilities
3. **Code Review**: All code changes should be reviewed before merging
4. **Input Validation**: Always validate and sanitize user input
5. **Authentication**: Use JWT tokens with appropriate expiration times
6. **Authorization**: Implement proper role-based access control
7. **HTTPS**: Always use HTTPS in deployment environments
8. **CORS**: Configure CORS properly to allow only trusted origins

---

## Security Features

### Current Implementation

✅ **Authentication**

- JWT-based authentication
- Token expiration (24 hours)
- Refresh token mechanism
- Secure password storage (BCrypt hashing on backend)

✅ **Authorization**

- Role-based access control (RBAC)
- Route guards protecting sensitive pages
- API endpoint protection

✅ **Data Protection**

- Input validation on all forms
- XSS protection enabled
- CSRF protection (via SameSite cookies)
- SQL injection prevention (via JPA/Hibernate)

✅ **Network Security**

- CORS configuration
- HTTPS support ready
- Security headers configured in Nginx

✅ **Frontend Security**

- No sensitive data in localStorage (only JWT tokens)
- Tokens cleared on logout
- Automatic token cleanup on expiration
- No inline scripts (CSP-ready)

### Known Limitations

⚠️ **Development Mode**

- Development server runs on HTTP (use HTTPS in deployment environment)
- Debug mode enabled (disable in live environment)
- Verbose error messages (sanitize in deployment environment)

⚠️ **Token Storage**

- JWT tokens stored in localStorage (consider httpOnly cookies for enhanced security)
- No refresh token rotation (planned for v1.1)

---

## Security Checklist for Deployment

### Before Deploying to Live Environment

- [ ] Change default JWT secret to a strong random value
- [ ] Set strong database passwords
- [ ] Enable HTTPS with valid SSL certificates
- [ ] Configure CORS to allow only your domain
- [ ] Disable debug mode and verbose logging
- [ ] Set secure headers in Nginx
- [ ] Configure rate limiting
- [ ] Set up monitoring and alerting
- [ ] Review and minimize exposed API endpoints
- [ ] Enable database backups
- [ ] Set up firewall rules
- [ ] Review user permissions and roles

### Environment Variables to Secure

```bash
# Generate secure JWT secret
openssl rand -base64 64

# Set in .env file
JWT_SECRET=<your-secure-secret-here>
DB_PASSWORD=<strong-database-password>
```

---

## Vulnerability Scanning

### Automated Scans

We use the following tools to scan for vulnerabilities:

- **npm audit**: Scans npm dependencies for known vulnerabilities
- **Snyk**: Continuous security monitoring
- **GitHub Dependabot**: Automated dependency updates

### Running Security Scans Locally

```bash
# Check for vulnerable npm packages
npm audit

# Fix vulnerabilities automatically (if possible)
npm audit fix

# Generate detailed security report
npm audit --json > security-report.json
```

---

## Security Updates

### How We Handle Security Issues

1. **Discovery**: Vulnerability is discovered or reported
2. **Assessment**: We assess severity and impact
3. **Fix Development**: We develop and test a fix
4. **Release**: We release a patch version
5. **Notification**: We notify users via:
   - GitHub Security Advisory
   - Release notes
   - Email (for critical issues)

### Staying Informed

- **Watch** this repository to get notifications
- **Star** to bookmark and track updates
- **Follow** release notes for security patches
- **Subscribe** to security advisories

---

## Compliance

This project follows security best practices from:

- OWASP Top 10
- SANS Top 25
- CWE/SANS Top 25 Most Dangerous Software Errors

---

## Security-Related Dependencies

### Critical Security Dependencies

- **@angular/core**: Core Angular framework (security updates)
- **rxjs**: Reactive programming (memory leak prevention)
- **zone.js**: Change detection (security patches)

### Regular Updates

We update dependencies regularly:

- **Monthly**: Regular dependency updates
- **Weekly**: Security patches
- **Immediate**: Critical security vulnerabilities

---

## Contact

For security concerns:

- **Email**: [piyushkumar30066@gmail.com](mailto:piyushkumar30066@gmail.com)
- **Security Advisories**: [GitHub Security](https://github.com/YOUR_USERNAME/fitness-frontend/security)

For general questions:

- **Issues**: [GitHub Issues](https://github.com/YOUR_USERNAME/fitness-frontend/issues)
- **Discussions**: [GitHub Discussions](https://github.com/YOUR_USERNAME/fitness-frontend/discussions)

---

## Hall of Fame

We would like to thank the following people for responsibly disclosing security issues:

- None yet - be the first!

---

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Angular Security Guide](https://angular.io/guide/security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Web Security Cheat Sheet](https://cheatsheetseries.owasp.org/index.html)

---

**Last Updated**: January 27, 2025


# Deployment & Environment Setup

**Project:** Lost & Found Bangladesh
**Backend:** Java, Spring Boot
**Frontend:** React
**Database:** MySQL
**Version:** 1.0
**Status:** Existing MVP + Planned Production Improvements

## 1. Overview

This document explains how to configure, run, build, and deploy the application.

**Deployment environments:**

| Environment | Purpose                            |
| ----------- | ---------------------------------- |
| Development | Local development and debugging    |
| Testing     | Automated testing and verification |
| Production  | Public application deployment      |

Production configuration must be isolated from development and testing.

## 2. Prerequisites

Required software:

- Java 21
- Maven or Maven Wrapper
- MySQL
- Node.js and npm
- Git

Optional:

- Docker and Docker Compose
- Postman
- MySQL Workbench

## 3. Repository Setup

Clone the repository:

```bash
git clone https://github.com/gr309/lost-and-found.git
cd lost-and-found
```

Check the current branch:

```bash
git branch
```

Install dependencies using the backend and frontend instructions below.

## 4. Backend Environment Configuration

Create a local `.env` file using `.env.example` as a reference.

Example:

```env
APP_ENV=dev
APP_DEBUG=true
PORT=8080

DB_URL=jdbc:mysql://localhost:3306/lostfound
DB_USERNAME=lostfound
DB_PASSWORD=replace_me

JWT_SECRET=replace_with_a_secure_secret

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

**Important:**

- Never commit `.env`.
- Never commit production credentials.
- Use different secrets for each environment.
- Rotate exposed credentials.
- Verify that Spring Boot actually loads the configured environment variables.

## 5. Database Setup

Start MySQL and create the database:

```sql
CREATE DATABASE IF NOT EXISTS lostfound
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Create a dedicated database user with appropriate permissions.

Do not use the MySQL root account for application access.

### Database Migrations

Use versioned migrations for schema changes.

Recommended location:

```text
src/main/resources/db/migration/
```

Apply migrations during the deployment process.

**Production:** Avoid relying on `spring.jpa.hibernate.ddl-auto=update`.

Use migration-controlled schema changes and Hibernate validation where appropriate.

## 6. Run Backend Locally

From the backend project directory:

```bash
./mvnw spring-boot:run
```

Alternatively:

```bash
mvn spring-boot:run
```

Default local API:

```text
http://localhost:8080
```

Verify the application using an existing public endpoint:

```bash
curl http://localhost:8080/api/items/
```

The application should return an HTTP response without a connection error.

## 7. Run Frontend Locally

Navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Typical local frontend URL:

```text
http://localhost:5173
```

The exact frontend directory and scripts must be verified against `package.json`.

### Frontend Environment

For a Vite-based React application:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Frontend environment variables must never contain database passwords, JWT signing secrets, or private API keys.

## 8. Build Application

### Backend

```bash
./mvnw clean package
```

Generated JAR files are typically located under:

```text
target/
```

Run the generated application:

```bash
java -jar target/<application-name>.jar
```

Replace `<application-name>` with the actual generated filename.

### Frontend

```bash
npm run build
```

For Vite projects, the production output is typically generated in:

```text
dist/
```

## 9. Production Architecture

Recommended deployment model:

```text
                  Internet
                     |
                     v
                   HTTPS
                     |
          +----------+----------+
          |                     |
          v                     v
     React Frontend       Spring Boot API
          |                     |
          |                     v
          |                  MySQL
          |                     |
          |                     v
          |                File Storage
          |
          v
     Map Tile Provider
```

### Existing Deployment

Previously used services:

| Component    | Platform                              |
| ------------ | ------------------------------------- |
| Backend      | Render                                |
| Frontend     | Vercel                                |
| Database     | MySQL                                 |
| File storage | Existing external storage integration |

Production infrastructure may change as the project grows.

## 10. Backend Deployment

General production deployment steps:

1. Build the application.
2. Configure production environment variables.
3. Connect the production database securely.
4. Apply database migrations.
5. Configure approved CORS origins.
6. Configure HTTPS.
7. Start the application.
8. Verify health and API endpoints.
9. Monitor logs and errors.

### Example Production Configuration

```env
APP_ENV=production
APP_DEBUG=false
PORT=8080

DB_URL=jdbc:mysql://<db-host>:3306/<db-name>
DB_USERNAME=<db-user>
DB_PASSWORD=<db-password>

JWT_SECRET=<secure-production-secret>

CORS_ALLOWED_ORIGINS=https://<frontend-domain>
```

Use the actual database provider's SSL/TLS requirements.

Secrets should be configured through the hosting provider's environment or secret-management system.

## 11. Frontend Deployment

General steps:

1. Configure the production API base URL.
2. Install dependencies.
3. Run the production build.
4. Deploy generated assets.
5. Configure the frontend domain.
6. Enable HTTPS.
7. Verify frontend-to-backend communication.

### CORS Verification

The backend must allow the deployed frontend origin.

Do not include URL paths in the CORS origin configuration.

## 12. Docker — Optional

Docker may be used to standardize deployment.

Suggested services:

```text
docker-compose.yml
    |
    +-- backend
    |
    +-- mysql
```

The frontend may be hosted separately.

If Docker is introduced, ensure:

- Persistent database volumes
- Container health checks
- Environment-specific configuration
- Restart policies
- Appropriate network restrictions

## 13. CI/CD Pipeline

Recommended GitHub Actions workflow:

```text
Push / Pull Request
        |
        v
Checkout Repository
        |
        v
Setup Java
        |
        v
Compile Backend
        |
        v
Run Tests
        |
        v
Build Application
        |
        v
Deployment Approval
        |
        v
Deploy Production
```

Frontend checks should include:

- Dependency installation
- Linting
- Automated tests, where configured
- Production build

Deploy production changes only after required checks pass.

## 14. Database Backup & Recovery

Production databases require regular backups.

### Requirements

- Automated backups
- Secure backup storage
- Defined retention policy
- Periodic restoration tests
- Restricted backup access

Backups should be created before significant database migrations.

A backup is not considered reliable until restoration has been tested.

## 15. Logging & Monitoring

Monitor:

| Metric                 | Purpose                  |
| ---------------------- | ------------------------ |
| API response time      | Performance              |
| HTTP error rate        | Reliability              |
| Database connectivity  | Availability             |
| Application exceptions | Debugging                |
| Matching job failures  | SmartMatch reliability   |
| Storage errors         | Image upload reliability |
| Resource usage         | Infrastructure capacity  |

Never log passwords, JWT tokens, or sensitive verification information.

## 16. Health Checks

Recommended Spring Boot Actuator endpoints:

```http
GET /actuator/health
```

Expose only necessary actuator endpoints publicly.

Detailed health information should be restricted.

## 17. Troubleshooting

### Backend Does Not Start

Check:

- Java version
- Environment variables
- Database connection
- Port availability
- Application logs

### Database Connection Failed

Verify:

- Database hostname
- Port
- Credentials
- Database existence
- Network access
- SSL/TLS configuration

### Frontend Cannot Access Backend

Verify:

- API base URL
- CORS configuration
- HTTPS configuration
- Backend availability
- Browser network errors

### HTTP 401

Check:

- JWT presence
- Token validity
- Token expiration
- Security route configuration

### Image Upload Failed

Check:

- Storage configuration
- Upload limits
- File validation
- Storage service availability

## 18. Production Release Checklist

- [ ] Backend builds successfully
- [ ] Frontend builds successfully
- [ ] Automated tests pass
- [ ] Database migrations verified
- [ ] Production secrets configured
- [ ] Exposed credentials rotated
- [ ] HTTPS enabled
- [ ] CORS configured correctly
- [ ] Authentication verified
- [ ] Authorization verified
- [ ] Image storage working
- [ ] Database backups enabled
- [ ] Monitoring configured
- [ ] Critical APIs tested
- [ ] Documentation updated

## 19. Rollback Strategy

If deployment fails:

1. Stop or disable the failed release.
2. Restore the previous working application version.
3. Investigate application and database compatibility.
4. Restore data only when necessary and after assessing data-loss risks.
5. Verify critical functionality.
6. Document the incident.

Database migrations should be designed to support safe application rollback whenever possible.

## 20. Related Documentation

- `03-ARCHITECTURE.md` — System architecture
- `04-DATABASE.md` — Database schema and migrations
- `05-API.md` — API contracts
- `08-SECURITY.md` — Production security requirements
- `09-TESTING.md` — Verification and testing

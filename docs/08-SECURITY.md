
# Security & Privacy

**Project:** Lost & Found Bangladesh
**Backend:** Spring Boot + Spring Security
**Authentication:** JWT
**Version:** 1.0
**Status:** Existing Security + Planned Improvements

## 1. Overview

The platform must protect user accounts, personal information, item reports, ownership claims, and recovery activities.

Security is enforced primarily by the backend. Frontend restrictions are only for user experience.

## 2. Authentication

The application uses JWT-based authentication.

### Authentication Flow

```text
User → Login Request
     → AuthController
     → AuthenticationManager
     → UserRepository
     → Password Verification
     → JWT Generation
     → Response
```

Protected requests include:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Security Requirements

- Hash passwords using a secure password encoder.
- Validate JWT signatures and expiration.
- Reject invalid or expired tokens.
- Never return passwords or password hashes.
- Store secrets in environment variables or a secret manager.
- Use HTTPS in production.

**Relevant files:** `SecurityConfig.java`, authentication controller, JWT utilities, and user authentication components.

Exact implementation classes must be verified against the source code.

## 3. Authorization

### User Roles

| Role      | Permissions                                       |
| --------- | ------------------------------------------------- |
| Guest     | Browse public reports and search                  |
| User      | Create reports, manage own reports, submit claims |
| Moderator | Review flagged content and disputes               |
| Admin     | Manage platform operations                        |

### Authorization Rules

1. Only authenticated users can create reports.
2. Only report owners or authorized moderators can modify reports.
3. Users can access only claims they are permitted to view.
4. Only authorized parties can approve or reject claims.
5. Users can access only their own private notifications.
6. Administrative endpoints require appropriate roles.
7. Every protected operation must verify permissions server-side.

## 4. API Access Matrix

| Endpoint                           | Guest | User                    | Moderator/Admin |
| ---------------------------------- | ----- | ----------------------- | --------------- |
| `GET /api/items/`                | ✅    | ✅                      | ✅              |
| `GET /api/items/{id}/`           | ✅    | ✅                      | ✅              |
| `POST /api/items/`               | ❌    | ✅                      | ✅              |
| `PUT /api/items/{id}/`           | ❌    | Owner only              | Authorized      |
| `DELETE /api/items/{id}/`        | ❌    | Owner only              | Authorized      |
| `POST /api/claims/`              | ❌    | ✅                      | ✅              |
| `GET /api/claims/`               | ❌    | Own claims              | Authorized      |
| `POST /api/claims/{id}/approve/` | ❌    | Authorized report owner | Authorized      |
| `POST /api/claims/{id}/reject/`  | ❌    | Authorized report owner | Authorized      |
| `GET /api/matches/my/`           | ❌    | Own matches             | Authorized      |
| `/api/admin/**`                  | ❌    | ❌                      | Authorized      |

The table describes intended access rules. New routes and roles must be implemented and verified.

## 5. Resource Ownership

Authentication alone is insufficient.

Services must verify that a user is authorized to access or modify a particular resource.

### Example: Update Item

```text
PUT /api/items/{id}/
        ↓
JWT Authentication
        ↓
ItemController
        ↓
ItemService
        ↓
Load Item
        ↓
Compare Reporter with Authenticated User
        ↓
Authorized?
   ┌────┴────┐
   │         │
  YES        NO
   │         │
 Update    HTTP 403
```

Never trust a user ID provided in a request body as proof of identity.

## 6. Ownership Claim Security

An item match does not establish ownership.

### Verification Requirements

- Keep distinguishing characteristics private.
- Restrict access to submitted evidence.
- Prevent users from claiming their own found reports.
- Validate claim status transitions.
- Prevent unauthorized approval or rejection.
- Support review of suspicious claims.

### Claim Verification Flow

```text
Claim Submitted
      ↓
Private Evidence Collected
      ↓
Authorized Review
      ↓
Approve / Reject
      ↓
Notify Claimant
```

An approved claim must not automatically mark a physical handover as completed.

## 7. Location Privacy

The platform uses geographical coordinates for searching and matching.

Exact locations can reveal sensitive information.

### Rules

1. Do not publicly expose users' live GPS locations.
2. Store event locations separately from current user locations.
3. Display approximate map markers when appropriate.
4. Restrict access to private handover locations.
5. Request browser location permission only when necessary.
6. Do not continuously track users.

**Public location example:**

```json
{
  "area": "Shaheb Bazar",
  "district": "Rajshahi",
  "precision": "APPROXIMATE"
}
```

Exact coordinates may remain available internally for authorized matching operations.

## 8. Image and Document Security

Uploaded images may contain identity documents or sensitive personal information.

### Requirements

- Validate file types and sizes.
- Generate safe storage filenames.
- Restrict access to private images.
- Avoid exposing raw storage credentials.
- Prevent executable file uploads.
- Redact sensitive information from public document previews.
- Remove unnecessary metadata where appropriate.

Private verification documents should not be stored as publicly accessible files.

## 9. Input Validation

Validate all incoming data.

Examples:

- Required fields
- Maximum text length
- Valid enum values
- Valid dates
- Coordinate ranges
- Allowed file types
- Positive search radius
- Pagination limits

Use DTO validation and service-level business validation.

Database constraints provide an additional integrity layer.

## 10. Common Security Threats

| Threat                | Mitigation                                            |
| --------------------- | ----------------------------------------------------- |
| Broken access control | Backend authorization and ownership checks            |
| SQL injection         | Parameterized queries and JPA                         |
| XSS                   | Safe frontend rendering and output handling           |
| CSRF                  | Appropriate protection based on authentication design |
| Brute-force login     | Rate limiting and monitoring                          |
| Malicious uploads     | File validation and restricted storage                |
| JWT theft             | HTTPS, secure token handling, short-lived credentials |
| Data leakage          | Response DTOs and restricted access                   |
| Fake ownership claims | Private evidence and verification                     |
| Spam reports          | Rate limiting and moderation                          |

## 11. CORS Configuration

Allow only approved frontend origins.

Example environment variable:

```env
CORS_ALLOWED_ORIGINS=https://example.com,http://localhost:5173
```

The actual production domain must replace the placeholder.

Do not use unrestricted origins for credentialed production requests.

## 12. Environment Variables

Sensitive configuration must not be committed to Git.

Example `.env.example`:

```env
APP_ENV=development
PORT=8080

DB_URL=jdbc:mysql://localhost:3306/lostfound
DB_USERNAME=lostfound
DB_PASSWORD=replace_me

JWT_SECRET=replace_with_secure_secret

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

Use strong, unique production secrets.

If credentials have previously been exposed, rotate them before production deployment.

## 13. Logging and Auditing

Log important security-related actions:

- Authentication failures
- Unauthorized access attempts
- Report modification
- Claim approval/rejection
- Administrative actions
- Recovery disputes

Do not log:

- Passwords
- JWT tokens
- Private identity documents
- Sensitive ownership evidence
- Database credentials

Administrative audit records should include the actor, action, target resource, and timestamp.

## 14. Error Handling

Security-related errors should use appropriate HTTP status codes.

| Status | Meaning                           |
| ------ | --------------------------------- |
| 400    | Invalid input                     |
| 401    | Unauthenticated                   |
| 403    | Unauthorized operation            |
| 404    | Resource unavailable or not found |
| 409    | Conflicting operation             |
| 429    | Too many requests                 |

Do not expose internal stack traces or sensitive implementation details in production responses.

## 15. Security Testing

Required test scenarios:

- Access protected API without JWT.
- Access API using expired JWT.
- Modify another user's report.
- Read another user's private claim.
- Approve a claim without authorization.
- Access another user's notifications.
- Upload invalid files.
- Submit duplicate claims.
- Bypass claim status transitions.
- Access restricted administrative endpoints.

Security tests should run as part of automated verification.

## 16. Production Security Checklist

- [ ] HTTPS enabled
- [ ] Production secrets configured securely
- [ ] Exposed credentials rotated
- [ ] JWT validation verified
- [ ] Authorization tests passing
- [ ] CORS restricted
- [ ] Input validation enabled
- [ ] Upload restrictions enforced
- [ ] Sensitive data protected
- [ ] Rate limiting configured
- [ ] Production error messages sanitized
- [ ] Database backups configured
- [ ] Security logging enabled

## 17. Related Documentation

- `03-ARCHITECTURE.md` — Security architecture
- `05-API.md` — API authentication and authorization
- `06-CODEBASE.md` — Security-related classes
- `09-TESTING.md` — Security testing
- `10-DEPLOYMENT.md` — Production configuration

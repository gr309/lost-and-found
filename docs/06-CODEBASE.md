
# Codebase Guide

**Project:** Lost & Found Bangladesh
**Backend:** Java, Spring Boot
**Base Package:** `com.learn.lostfound`
**Version:** 1.0
**Status:** Existing MVP + Planned Expansion

## 1. Purpose

This document explains how the backend code is organized, what each major class is responsible for, and how API requests move through the application.

For exact HTTP contracts, see `05-API.md`.

## 2. Project Structure

Current architectural layout, with planned additions:

```text
src/main/java/com/learn/lostfound/
│
├── LostfoundApplication.java
│
├── config/
│   └── SecurityConfig.java
│
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ItemController.java
│   ├── ClaimController.java
│   ├── NotificationController.java    # Verify
│   ├── SearchController.java          # Planned
│   ├── MatchController.java           # Planned
│   └── RecoveryController.java        # Planned
│
├── service/
│   ├── AuthService.java               # Verify
│   ├── UserService.java               # Verify
│   ├── ItemService.java
│   ├── ClaimService.java
│   ├── NotificationService.java
│   ├── SearchService.java             # Planned
│   ├── LocationService.java           # Planned
│   ├── MatchingService.java           # Planned
│   └── RecoveryService.java           # Planned
│
├── repository/
│   ├── UserRepository.java
│   ├── ItemRepository.java
│   ├── ClaimRepository.java
│   ├── NotificationRepository.java    # Verify
│   └── ItemMatchRepository.java       # Planned
│
├── entity/
│   ├── User.java
│   ├── Item.java
│   ├── Claim.java
│   ├── Notification.java              # Verify
│   ├── ItemLocation.java              # Planned
│   └── ItemMatch.java                 # Planned
│
├── dto/
│   ├── ClaimRequest.java
│   ├── ClaimResponse.java
│   └── ...
│
├── enums/
│   ├── ItemType.java
│   ├── ClaimStatus.java
│   ├── NotificationType.java
│   └── ...
│
├── security/
│   └── ...                             # Verify JWT classes
│
└── exception/
    └── ...                             # Verify exception handlers
```

**Legend:**

- No annotation: Existing or previously referenced class; inspect source for exact behavior.
- `Verify`: Class name or presence requires verification.
- `Planned`: Proposed class, not confirmed as implemented.

Do not create duplicate classes if equivalent functionality already exists.

---

## 3. Application Entry Point

### `LostfoundApplication.java`

**Responsibility:** Start the Spring Boot application.

Typical responsibilities:

- Initialize Spring Boot.
- Discover Spring components.
- Load application configuration.
- Start the embedded web server.

**Depends on:** Spring Boot framework.

**Called by:** Application runtime.

---

## 4. Configuration Layer

### `config/SecurityConfig.java`

**Responsibility:** Configure HTTP security.

Expected responsibilities:

- Configure public and protected routes.
- Configure CORS.
- Configure stateless authentication.
- Integrate JWT authentication.
- Define authorization rules.

**Execution:**

```text
HTTP Request
    ↓
Spring Security Filter Chain
    ↓
Authentication
    ↓
Authorization
    ↓
Controller
```

Security configuration must be reviewed before adding new public APIs.

---

## 5. Controller Layer

Controllers receive HTTP requests, validate request data, and delegate operations to services.

| File                            | Responsibility                       | Main Dependency         |
| ------------------------------- | ------------------------------------ | ----------------------- |
| `AuthController.java`         | Registration and login               | Authentication logic    |
| `UserController.java`         | Current user information             | User service/repository |
| `ItemController.java`         | Item CRUD operations                 | `ItemService`         |
| `ClaimController.java`        | Claim submission and decisions       | `ClaimService`        |
| `NotificationController.java` | Notification APIs, if present        | `NotificationService` |
| `SearchController.java`       | Unified and nearby search — Planned | `SearchService`       |
| `MatchController.java`        | Matching results — Planned          | `MatchingService`     |
| `RecoveryController.java`     | Recovery management — Planned       | `RecoveryService`     |

### Controller Rules

Controllers should:

1. Accept requests.
2. Validate request DTOs.
3. Resolve authentication context where needed.
4. Call service methods.
5. Return appropriate HTTP responses.

Controllers should not directly implement database operations or complex business rules.

---

## 6. Service Layer

Services contain business logic and coordinate application operations.

### `ItemService.java`

**Responsibility:** Manage lost and found reports.

Expected operations:

- Retrieve reports.
- Retrieve an individual report.
- Create reports.
- Update reports.
- Delete or close reports.
- Validate report ownership.

**Dependencies:** Primarily `ItemRepository` and user/authentication components.

**Future integration:** Trigger SmartMatch after successful report creation or relevant updates.

### `ClaimService.java`

**Responsibility:** Manage ownership claims.

Previously observed dependencies:

```text
ClaimService
    ├── ClaimRepository
    ├── ItemRepository
    ├── UserRepository
    └── NotificationService
```

Main operations:

- Submit ownership claims.
- Retrieve claims.
- Approve claims.
- Reject claims.
- Enforce claim authorization.
- Validate claim state transitions.
- Trigger relevant notifications.

**Important:** An approved claim does not necessarily mean the item has been physically returned.

### `NotificationService.java`

**Responsibility:** Create and manage system notifications.

Expected operations:

- Create notifications.
- Retrieve user notifications.
- Mark notifications as read.
- Handle notification events.

Potential notification events:

```text
CLAIM_CREATED
CLAIM_APPROVED
CLAIM_REJECTED
MATCH_FOUND       # Planned
RECOVERY_COMPLETED # Planned
```

Actual enum values must follow the source code.

### `SearchService.java` — Planned

**Responsibility:** Unified item discovery.

Operations:

- Normalize search queries.
- Apply structured filters.
- Retrieve eligible reports.
- Rank search results.
- Return paginated data.

### `LocationService.java` — Planned

**Responsibility:** Geographical operations.

Operations:

- Validate coordinates.
- Resolve location information.
- Calculate geographical distance.
- Retrieve nearby reports.
- Handle approximate locations.

### `MatchingService.java` — Planned

**Responsibility:** Automatically identify potential lost/found matches.

Operations:

- Retrieve matching candidates.
- Compare report attributes.
- Calculate similarity scores.
- Persist matching results.
- Trigger notifications.

### `RecoveryService.java` — Planned

**Responsibility:** Manage verified item handovers.

Operations:

- Validate claim eligibility.
- Create recovery records.
- Track handover status.
- Confirm completed returns.
- Handle disputes.

---

## 7. Repository Layer

Repositories provide database access through Spring Data JPA.

| File                            | Entity       | Responsibility                   |
| ------------------------------- | ------------ | -------------------------------- |
| `UserRepository.java`         | User         | User lookup and persistence      |
| `ItemRepository.java`         | Item         | Report queries and persistence   |
| `ClaimRepository.java`        | Claim        | Claim queries and persistence    |
| `NotificationRepository.java` | Notification | Notification storage, if present |
| `ItemMatchRepository.java`    | ItemMatch    | Match storage — Planned         |

Repositories should focus on persistence rather than business decisions.

For complex search operations, use appropriate query methods, specifications, or dedicated query components.

---

## 8. Entity Layer

Entities map Java objects to database tables.

| File                  | Responsibility                           |
| --------------------- | ---------------------------------------- |
| `User.java`         | User accounts and identity               |
| `Item.java`         | Lost and found reports                   |
| `Claim.java`        | Ownership claims                         |
| `Notification.java` | Notification records, if present         |
| `ItemLocation.java` | Geographic report information — Planned |
| `ItemMatch.java`    | Potential lost/found matches — Planned  |

Entities define:

- Persistent fields.
- Relationships.
- Database constraints.
- Entity-level behavior where appropriate.

**Rule:** Do not expose sensitive entity fields directly through public APIs.

See `04-DATABASE.md` for the proposed schema.

---

## 9. DTO Layer

DTOs define API request and response structures.

### Existing Examples

| File                   | Purpose               |
| ---------------------- | --------------------- |
| `ClaimRequest.java`  | Claim submission data |
| `ClaimResponse.java` | Claim response data   |

Other DTOs must be identified through source-code inspection.

### Proposed DTOs

```text
ItemSearchRequest
ItemSearchResponse
NearbySearchResponse
MatchResponse
MatchExplanationResponse
RecoveryRequest
RecoveryResponse
```

**Rules:**

- Request DTOs validate incoming data.
- Response DTOs expose only authorized information.
- Internal entities should not be returned indiscriminately.

---

## 10. Enum Layer

Enums represent controlled application states.

### `ItemType.java`

Previously identified values:

```java
LOST
FOUND
```

### `ClaimStatus.java`

The actual enum must be verified before modifying claim workflows.

### `NotificationType.java`

Defines supported notification categories.

**Rule:** State transitions must be validated in services; an enum alone does not enforce business rules.

---

# 11. API Execution Paths

## 11.1 Registration

**Endpoint:** `POST /api/auth/signup/`

```text
AuthController
    ↓
Authentication Logic
    ↓
UserRepository
    ↓
MySQL
    ↓
Registration Response
```

Authentication service names and methods require source verification.

## 11.2 Login

**Endpoint:** `POST /api/auth/signin/`

```text
AuthController
    ↓
AuthenticationManager / Authentication Logic
    ↓
UserRepository
    ↓
Password Verification
    ↓
JWT Generation
    ↓
Response
```

## 11.3 Create Item

**Endpoint:** `POST /api/items/`

```text
ItemController
    ↓
ItemService
    ├── Validate Data
    ├── Identify Reporter
    └── Build Item Entity
    ↓
ItemRepository.save()
    ↓
MySQL
    ↓
Response
```

**Future:** Trigger matching after transaction commit.

## 11.4 Submit Claim

**Endpoint:** `POST /api/claims/`

```text
ClaimController
    ↓
ClaimService
    ├── ItemRepository
    ├── UserRepository
    ├── ClaimRepository
    └── NotificationService
    ↓
MySQL
    ↓
ClaimResponse
```

## 11.5 Approve or Reject Claim

**Endpoints:**

```http
POST /api/claims/{id}/approve/
POST /api/claims/{id}/reject/
```

**Execution:**

```text
ClaimController
    ↓
ClaimService
    ↓
Load Claim
    ↓
Verify Authorization
    ↓
Validate Status Transition
    ↓
Update Claim
    ↓
Send Notification
    ↓
Response
```

## 11.6 Unified Search — Planned

```text
SearchController
    ↓
SearchService
    ↓
Search Query Builder
    ↓
ItemRepository / SearchRepository
    ↓
MySQL
    ↓
Ranked Results
```

## 11.7 SmartMatch — Planned

```text
Item Created/Updated
    ↓
Transaction Commit
    ↓
Matching Trigger
    ↓
MatchingService
    ├── Candidate Retrieval
    ├── Attribute Similarity
    ├── Location Similarity
    ├── Time Similarity
    └── Score Calculation
    ↓
ItemMatchRepository
    ↓
MySQL
    ↓
NotificationService
```

---

# 12. Dependency Rules

Allowed dependency direction:

```text
Controller → Service → Repository → Database
```

Additional rules:

1. Controllers must not bypass services for business operations.
2. Repositories must not call controllers.
3. Services must avoid circular dependencies.
4. External integrations should be isolated behind services.
5. Security checks must be enforced server-side.
6. Matching logic should remain separate from claim verification.
7. Shared utility classes should remain small and focused.

---

# 13. Transactions

Use `@Transactional` for operations requiring multiple related database updates.

Examples:

- Claim approval involving multiple records.
- Recovery completion.
- Match persistence with related metadata.

External network operations should generally not execute inside long-running database transactions.

Matching should be triggered only after the corresponding report transaction commits successfully.

---

# 14. Exceptions and Validation

Expected responsibilities:

| Component                | Purpose                          |
| ------------------------ | -------------------------------- |
| Request DTO validation   | Validate incoming fields         |
| Service validation       | Enforce business rules           |
| Security configuration   | Authentication and authorization |
| Global exception handler | Standardize errors               |
| Database constraints     | Protect data integrity           |

Application errors should follow the conventions documented in `05-API.md`.

---

# 15. Adding a New Feature

Recommended implementation sequence:

```text
1. Define Requirement
        ↓
2. Design Database Changes
        ↓
3. Create Entity / Migration
        ↓
4. Create Repository
        ↓
5. Create Request/Response DTOs
        ↓
6. Implement Service
        ↓
7. Implement Controller
        ↓
8. Configure Authorization
        ↓
9. Write Tests
        ↓
10. Update Documentation
```

Do not modify stable existing APIs unnecessarily.

---

# 16. Codebase Documentation Maintenance

Whenever functionality changes:

- Update affected API contracts.
- Update class responsibilities.
- Update execution paths.
- Update database documentation if schema changes.
- Update tests.
- Mark planned features as implemented only after verification.

**Source code remains the authority for actual implementation.**

---

# 17. Related Documentation

| Document               | Purpose                 |
| ---------------------- | ----------------------- |
| `03-ARCHITECTURE.md` | System architecture     |
| `04-DATABASE.md`     | Database structure      |
| `05-API.md`          | API contracts and JSON  |
| `07-SMARTMATCH.md`   | Matching engine design  |
| `08-SECURITY.md`     | Security implementation |
| `09-TESTING.md`      | Testing strategy        |

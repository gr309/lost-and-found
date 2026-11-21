
# System Architecture

**Project:** Lost & Found Bangladesh
**Version:** 1.0
**Architecture:** Modular Monolith
**Status:** Existing MVP + Planned Nationwide Expansion

## 1. Architecture Overview

The system follows a layered architecture using Spring Boot for backend services, React for the frontend, and MySQL for persistent storage.

A modular monolith is preferred to keep development, testing, and deployment manageable.

```text
                   CLIENT
                     |
                     v
               React Frontend
                     |
                  REST API
                     |
                     v
              Spring Security
                JWT Filter
                     |
                     v
             Spring Boot Backend
                     |
        +------------+------------+
        |            |            |
        v            v            v
   Controllers    Services    Repositories
        |            |            |
        +------------+------------+
                     |
                     v
                   MySQL

External Services:
- File Storage
- Map Tiles / Geocoding
- Notification Delivery (Future)
```

## 2. Technology Stack

| Layer        | Technology                        |
| ------------ | --------------------------------- |
| Frontend     | React                             |
| Backend      | Java, Spring Boot                 |
| Security     | Spring Security, JWT              |
| Database     | MySQL                             |
| ORM          | Spring Data JPA / Hibernate       |
| Maps         | Leaflet, OpenStreetMap            |
| File Storage | Existing file storage integration |
| API          | REST / JSON                       |
| Testing      | JUnit, Mockito, Spring Boot Test  |
| Deployment   | Docker-compatible hosting         |

## 3. Backend Layers

### Controller Layer

**Responsibility:** Handle HTTP requests and responses.

Tasks:

- Receive API requests.
- Validate request DTOs.
- Call appropriate services.
- Return response DTOs and HTTP status codes.

Controllers must not contain complex business logic.

### Service Layer

**Responsibility:** Execute business rules.

Tasks:

- Validate business conditions.
- Enforce ownership and state transitions.
- Coordinate repository operations.
- Manage transactions.
- Trigger matching and notifications.

### Repository Layer

**Responsibility:** Database access.

Tasks:

- Execute CRUD operations.
- Retrieve filtered records.
- Perform custom queries.
- Support geographic candidate retrieval.

### Entity Layer

**Responsibility:** Represent persistent database models and relationships.

### DTO Layer

**Responsibility:** Define API request and response contracts.

DTOs prevent direct exposure of internal entities and sensitive fields.

### Security Layer

**Responsibility:** Authenticate users and enforce access permissions.

Components include:

- Spring Security configuration
- JWT authentication
- Role-based authorization
- Resource ownership validation

## 4. Backend Module Structure

Proposed package organization:

```text
src/main/java/com/learn/lostfound/
│
├── config/
├── security/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── enums/
├── exception/
├── matching/          # Planned
├── search/            # Planned
├── location/          # Planned
├── recovery/          # Planned
└── LostfoundApplication.java
```

Existing packages should be preserved unless a refactoring is necessary.

## 5. Module Responsibilities

| Module         | Responsibility                   |
| -------------- | -------------------------------- |
| Authentication | Registration, login, JWT         |
| User           | User profiles and permissions    |
| Item           | Lost/found report management     |
| Claim          | Ownership claims and approval    |
| Notification   | User notifications               |
| Search         | Unified search and filtering     |
| Location       | Coordinates and nearby searches  |
| Matching       | Automatic lost/found matching    |
| Recovery       | Verified handover management     |
| Moderation     | Abuse reports and dispute review |

Search, location, matching, recovery, and moderation modules are planned components unless verified as implemented.

## 6. Standard API Execution Flow

Example: Creating a lost-item report.

```text
POST /api/items/
       |
       v
Spring Security
       |
       v
ItemController
       |
       v
Request DTO Validation
       |
       v
ItemService
       |
       +-- Verify User
       +-- Validate Business Rules
       +-- Prepare Entity
       |
       v
ItemRepository
       |
       v
MySQL Database
       |
       v
Transaction Commit
       |
       v
Matching Trigger (Planned)
       |
       v
Response DTO
       |
       v
HTTP Response
```

The matching task should run after successful transaction commit to avoid processing reports that were rolled back.

## 7. SmartMatch Architecture

```text
New / Updated Item
        |
        v
Matching Trigger
        |
        v
Candidate Retrieval
        |
        v
Similarity Calculation
        |
        +-- Category
        +-- Title
        +-- Description
        +-- Attributes
        +-- Location
        +-- Time
        |
        v
Match Ranking
        |
        v
MatchRepository
        |
        v
NotificationService
```

SmartMatch is responsible for finding potential matches, not verifying legal ownership.

Detailed algorithms are documented in `07-SMARTMATCH.md`.

## 8. Geographic Search Architecture

```text
Frontend Map
      |
      v
Coordinates + Radius
      |
      v
SearchController
      |
      v
LocationSearchService
      |
      v
Geographic Database Query
      |
      v
Nearby Items
      |
      v
Public Response DTO
```

The system shall support:

- Latitude and longitude
- Radius-based searches
- Administrative location filters
- Approximate location handling
- Privacy-preserving map markers

## 9. Database Architecture

MySQL is the primary relational database.

The database stores:

- Users
- Items
- Item images
- Locations
- Claims
- Notifications
- Matches (planned)
- Recovery records (planned)

Database changes should be managed through versioned migrations.

Detailed entities and relationships are documented in `04-DATABASE.md`.

## 10. External Integrations

| Integration        | Purpose                              |
| ------------------ | ------------------------------------ |
| File storage       | Store item photographs               |
| Map tile provider  | Display interactive maps             |
| Geocoding provider | Convert place names into coordinates |
| Email/SMS provider | Optional notification delivery       |
| Routing provider   | Future JourneyMatch functionality    |

External integrations must have appropriate error handling and configuration.

## 11. Background Processing

Potentially expensive operations should not block normal API responses.

Examples:

- Automatic matching
- Notification delivery
- Image processing
- Scheduled match recalculation

Initial implementation may use Spring asynchronous execution or scheduled jobs.

A message queue may be introduced when required by workload or reliability needs.

## 12. Security Architecture

```text
HTTP Request
     |
     v
Security Filter Chain
     |
     v
JWT Authentication
     |
     v
Authorization Check
     |
     v
Controller
     |
     v
Service-Level Ownership Check
     |
     v
Business Operation
```

Authentication verifies identity.

Authorization determines whether the authenticated user may perform a specific operation.

Resource ownership must also be verified where applicable.

## 13. Error Handling

Centralized exception handling should convert application errors into consistent HTTP responses.

```text
Application Exception
        |
        v
Global Exception Handler
        |
        v
Standard Error Response
        |
        v
Frontend
```

Sensitive stack traces and internal details must not be exposed in production API responses.

## 14. Scalability Strategy

The platform will initially operate as a modular monolith.

Potential future optimizations include:

- Database indexes
- Paginated queries
- Geographic indexes
- Background workers
- Caching
- Object storage
- Horizontal application scaling

Microservices are not required for the initial nationwide release.

## 15. Architecture Rules

1. Controllers handle HTTP concerns, not business logic.
2. Services implement business rules.
3. Repositories handle persistence.
4. DTOs define external API contracts.
5. Entities must not be exposed directly when doing so risks unwanted field exposure.
6. Authorization must be enforced on the backend.
7. Database changes must use migrations.
8. External integrations must support failure handling.
9. Background tasks must be retry-safe.
10. Modules should avoid unnecessary circular dependencies.

## 16. Related Documentation

| Document             | Purpose                               |
| -------------------- | ------------------------------------- |
| `02-USER-FLOWS.md` | User journeys                         |
| `04-DATABASE.md`   | Database entities and relationships   |
| `05-API.md`        | API contracts                         |
| `06-CODEBASE.md`   | Exact class and file responsibilities |
| `07-SMARTMATCH.md` | Matching engine                       |
| `08-SECURITY.md`   | Authentication and authorization      |
| `10-DEPLOYMENT.md` | Deployment architecture               |

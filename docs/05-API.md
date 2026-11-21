
# REST API Documentation

**Project:** Lost & Found Bangladesh
**Backend:** Spring Boot
**API Style:** REST / JSON
**Version:** 1.0
**Base URL:** `http://localhost:8080`

## 1. API Conventions

**Content-Type:** `application/json`

Protected endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

HTTP status codes:

| Code | Meaning                                  |
| ---- | ---------------------------------------- |
| 200  | Successful request                       |
| 201  | Resource created                         |
| 204  | Successful request without response body |
| 400  | Invalid request                          |
| 401  | Authentication required                  |
| 403  | Permission denied                        |
| 404  | Resource not found                       |
| 409  | Resource/state conflict                  |
| 429  | Rate limit exceeded                      |
| 500  | Internal server error                    |

Examples below describe intended contracts. Fields not verified against source code are illustrative.

---

# 2. Authentication APIs

## 2.1 Register User

**Endpoint:** `POST /api/auth/signup/`
**Access:** Public
**Status:** Existing

### Request

```json
{
  "name": "Test User",
  "email": "user@example.com",
  "password": "StrongPassword123!"
}
```

### Response — 201 Created

```json
{
  "id": 1,
  "name": "Test User",
  "email": "user@example.com"
}
```

### Execution Flow

```text
POST /api/auth/signup/
    ↓
AuthController
    ↓
RegistrationService / AuthService
    ↓
UserRepository
    ↓
MySQL
    ↓
Response
```

**Validation:** Unique email, required fields, password requirements.

## 2.2 Login

**Endpoint:** `POST /api/auth/signin/`
**Access:** Public
**Status:** Existing

### Request

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123!"
}
```

### Response — 200 OK

```json
{
  "token": "<JWT_TOKEN>",
  "tokenType": "Bearer"
}
```

### Execution Flow

```text
AuthController
    ↓
AuthenticationManager / AuthService
    ↓
UserDetailsService
    ↓
UserRepository
    ↓
Password Verification
    ↓
JWT Generation
    ↓
Response
```

**Errors:** Invalid credentials, disabled account, invalid request.

## 2.3 Current User

**Endpoint:** `GET /api/user/me/`
**Access:** Authenticated
**Status:** Existing

### Response — 200 OK

```json
{
  "id": 1,
  "name": "Test User",
  "email": "user@example.com",
  "role": "USER"
}
```

**Flow:** JWT Filter → UserController → UserService → UserRepository → Response.

---

# 3. Item APIs

## 3.1 List Items

**Endpoint:** `GET /api/items/`
**Access:** Public
**Status:** Existing

### Query Parameters

| Parameter | Example | Purpose            |
| --------- | ------- | ------------------ |
| type      | FOUND   | Filter report type |
| category  | WALLET  | Filter category    |
| page      | 0       | Page number        |
| size      | 20      | Page size          |

Filtering and pagination parameters require verification or implementation.

### Response — 200 OK

```json
[
  {
    "id": 1,
    "title": "Black Wallet",
    "description": "Found near Shaheb Bazar",
    "type": "FOUND",
    "location": "Rajshahi",
    "eventDate": "2026-11-20"
  }
]
```

**Flow:** ItemController → ItemService → ItemRepository → MySQL.

## 3.2 Create Item Report

**Endpoint:** `POST /api/items/`
**Access:** Authenticated
**Status:** Existing

### Request

```json
{
  "title": "Black Wallet",
  "description": "Lost near Shaheb Bazar",
  "type": "LOST",
  "location": "Rajshahi",
  "eventDate": "2026-11-20"
}
```

### Response — 201 Created

```json
{
  "id": 12,
  "title": "Black Wallet",
  "description": "Lost near Shaheb Bazar",
  "type": "LOST",
  "location": "Rajshahi",
  "eventDate": "2026-11-20",
  "createdAt": "2026-11-21T15:30:00"
}
```

### Execution Flow

```text
POST /api/items/
    ↓
JWT Authentication
    ↓
ItemController
    ↓
ItemService
    ├── Validate Request
    ├── Resolve Authenticated User
    └── Create Item Entity
    ↓
ItemRepository.save()
    ↓
MySQL
    ↓
ItemResponse
    ↓
HTTP 201
```

**Future:** Trigger SmartMatch after the database transaction commits.

## 3.3 Get Item Details

**Endpoint:** `GET /api/items/{id}/`
**Access:** Public
**Status:** Existing

### Response — 200 OK

```json
{
  "id": 12,
  "title": "Black Wallet",
  "description": "Lost near Shaheb Bazar",
  "type": "LOST",
  "location": "Rajshahi",
  "eventDate": "2026-11-20"
}
```

**Errors:** `404` if the item does not exist or is not publicly accessible.

## 3.4 Update Item

**Endpoint:** `PUT /api/items/{id}/`
**Access:** Report owner
**Status:** Existing

### Request

```json
{
  "title": "Black Leather Wallet",
  "description": "Lost near Shaheb Bazar market",
  "type": "LOST",
  "location": "Rajshahi",
  "eventDate": "2026-11-20"
}
```

**Flow:** Controller → Service → Ownership Check → Repository → Response.

**Errors:** `400`, `401`, `403`, `404`.

## 3.5 Delete Item

**Endpoint:** `DELETE /api/items/{id}/`
**Access:** Report owner
**Status:** Existing

### Response

```http
204 No Content
```

**Flow:** Controller → Service → Ownership Check → Repository.

The deletion policy must be verified. Soft deletion is recommended for reports associated with claims or completed recoveries.

---

# 4. Image Upload API

**Status:** Existing image functionality; exact endpoint requires source verification.

### Proposed Endpoint

```http
POST /api/items/{id}/images/
Content-Type: multipart/form-data
Authorization: Bearer <JWT_TOKEN>
```

### Form Data

```text
file = wallet.jpg
```

### Example Response

```json
{
  "id": 5,
  "itemId": 12,
  "url": "<IMAGE_URL>"
}
```

### Execution Flow

```text
ItemImageController
    ↓
ImageService
    ├── Validate File
    ├── Check Item Ownership
    └── Upload to Storage
    ↓
ItemImageRepository
    ↓
MySQL
    ↓
Response
```

**Errors:** Unsupported file, oversized upload, unauthorized access, storage failure.

---

# 5. Claim APIs

## 5.1 Submit Claim

**Endpoint:** `POST /api/claims/`
**Access:** Authenticated
**Status:** Existing

### Request

```json
{
  "itemId": 12,
  "message": "I believe this wallet belongs to me."
}
```

### Response — 201 Created

```json
{
  "id": 7,
  "itemId": 12,
  "status": "PENDING"
}
```

### Execution Flow

```text
ClaimController
    ↓
ClaimService
    ├── Load Item
    ├── Load Claimant
    ├── Validate Claim Rules
    └── Create Claim
    ↓
ClaimRepository
    ↓
MySQL
    ↓
NotificationService
    ↓
Response
```

**Rules:**

- Authentication required.
- Claim must reference an eligible item.
- Duplicate or conflicting claims should be prevented.
- Ownership evidence must remain private.

Actual claim request fields must be checked against `ClaimRequest`.

## 5.2 List My Claims

**Endpoint:** `GET /api/claims/`
**Access:** Authenticated
**Status:** Existing

### Example Response

```json
[
  {
    "id": 7,
    "itemId": 12,
    "status": "PENDING"
  }
]
```

**Flow:** ClaimController → ClaimService → ClaimRepository.

Only claims accessible to the authenticated user shall be returned.

## 5.3 Approve Claim

**Endpoint:** `POST /api/claims/{id}/approve/`
**Access:** Authorized report owner
**Status:** Existing

### Response

```json
{
  "id": 7,
  "status": "APPROVED"
}
```

**Flow:** ClaimController → ClaimService → Authorization → Update Status → NotificationService.

An approved claim should not automatically imply that physical handover has occurred.

## 5.4 Reject Claim

**Endpoint:** `POST /api/claims/{id}/reject/`
**Access:** Authorized report owner
**Status:** Existing

### Response

```json
{
  "id": 7,
  "status": "REJECTED"
}
```

**Flow:** ClaimController → ClaimService → Authorization → Update Status → NotificationService.

---

# 6. Unified Search API — Planned

**Endpoint:** `GET /api/search/items/`
**Access:** Public

### Example Request

```http
GET /api/search/items/?q=black%20wallet&type=FOUND&district=Rajshahi&page=0&size=20
```

### Parameters

| Parameter | Type    | Description      |
| --------- | ------- | ---------------- |
| q         | String  | Search text      |
| type      | Enum    | LOST or FOUND    |
| category  | String  | Item category    |
| district  | String  | District         |
| fromDate  | Date    | Start date       |
| toDate    | Date    | End date         |
| lat       | Double  | Latitude         |
| lng       | Double  | Longitude        |
| radius    | Integer | Radius in meters |
| page      | Integer | Page             |
| size      | Integer | Page size        |

### Response — 200 OK

```json
{
  "content": [
    {
      "id": 12,
      "title": "Black Wallet",
      "type": "FOUND",
      "location": "Rajshahi",
      "relevanceScore": 86.5
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1
}
```

### Execution Flow

```text
SearchController
    ↓
SearchService
    ├── Validate Filters
    ├── Normalize Query
    ├── Retrieve Candidates
    └── Rank Results
    ↓
ItemRepository / SearchRepository
    ↓
MySQL
    ↓
Paginated Response
```

---

# 7. Nearby Search API — Planned

**Endpoint:** `GET /api/search/nearby/`
**Access:** Public

### Request

```http
GET /api/search/nearby/?lat=24.3705&lng=88.6042&radius=5000&type=FOUND
```

### Response — 200 OK

```json
{
  "items": [
    {
      "id": 18,
      "title": "Found Wallet",
      "type": "FOUND",
      "distanceMeters": 650,
      "location": {
        "area": "Shaheb Bazar",
        "district": "Rajshahi"
      }
    }
  ]
}
```

### Execution Flow

```text
SearchController
    ↓
LocationSearchService
    ↓
Geographic Query
    ↓
ItemRepository
    ↓
MySQL
    ↓
Nearby Results
```

**Validation:** Latitude `-90` to `90`, longitude `-180` to `180`, and a positive radius within configured limits.

Exact private coordinates must not be returned by public endpoints.

---

# 8. SmartMatch APIs — Planned

## 8.1 Get My Matches

**Endpoint:** `GET /api/matches/my/`
**Access:** Authenticated

### Response

```json
[
  {
    "id": 25,
    "lostItemId": 12,
    "foundItemId": 18,
    "matchScore": 89.5,
    "status": "CANDIDATE"
  }
]
```

### Execution Flow

```text
MatchController
    ↓
MatchService
    ↓
MatchRepository
    ↓
MySQL
    ↓
MatchResponse
```

## 8.2 Get Match Details

**Endpoint:** `GET /api/matches/{id}/`
**Access:** Authorized participant

### Response

```json
{
  "id": 25,
  "matchScore": 89.5,
  "algorithmVersion": "rules-v1",
  "scoreBreakdown": {
    "category": 100,
    "title": 90,
    "description": 85,
    "attributes": 90,
    "location": 80,
    "time": 95
  }
}
```

Sensitive attributes must be excluded from public explanations.

## 8.3 Automatic Matching Process

SmartMatch runs internally after eligible report changes.

```text
Item Created / Updated
    ↓
Transaction Committed
    ↓
MatchingService
    ↓
CandidateRetrievalService
    ↓
SimilarityScoringService
    ↓
MatchRepository
    ↓
NotificationService
```

Matching results are suggestions, not proof of ownership.

---

# 9. Recovery APIs — Planned

## 9.1 Start Recovery

**Endpoint:** `POST /api/recoveries/`
**Access:** Authorized verified participants

### Request

```json
{
  "claimId": 7,
  "handoverMethod": "PUBLIC_LOCATION"
}
```

### Response — 201 Created

```json
{
  "id": 3,
  "claimId": 7,
  "status": "PENDING"
}
```

### Flow

```text
RecoveryController
    ↓
RecoveryService
    ├── Verify Claim Status
    ├── Verify Authorization
    └── Create Recovery
    ↓
RecoveryRepository
    ↓
MySQL
```

## 9.2 Confirm Recovery

**Endpoint:** `POST /api/recoveries/{id}/confirm/`
**Access:** Authorized participants

### Response

```json
{
  "id": 3,
  "status": "COMPLETED"
}
```

**Rule:** Confirmation requirements must be satisfied before marking the item recovered.

---

# 10. Notification APIs

**Status:** Notification functionality exists; exact routes require verification.

### Proposed Endpoints

| Method | Endpoint                          | Purpose               |
| ------ | --------------------------------- | --------------------- |
| GET    | `/api/notifications/`           | List my notifications |
| PATCH  | `/api/notifications/{id}/read/` | Mark as read          |
| PATCH  | `/api/notifications/read-all/`  | Mark all as read      |

### Example Response

```json
[
  {
    "id": 10,
    "type": "MATCH_FOUND",
    "title": "Potential Match Found",
    "message": "A similar found item was reported.",
    "isRead": false
  }
]
```

### Flow

```text
NotificationController
    ↓
NotificationService
    ↓
NotificationRepository
    ↓
MySQL
    ↓
Response
```

Users must only access their own notifications.

---

# 11. Standard Error Response — Proposed

All APIs should eventually use a consistent error format.

```json
{
  "timestamp": "2026-11-21T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid item type",
  "path": "/api/items/"
}
```

### Common Error Cases

| Scenario                       | Status |
| ------------------------------ | ------ |
| Invalid request body           | 400    |
| Missing/invalid JWT            | 401    |
| Unauthorized resource access   | 403    |
| Item does not exist            | 404    |
| Duplicate or conflicting claim | 409    |
| Rate limit exceeded            | 429    |
| Unexpected error               | 500    |

Do not expose stack traces or sensitive internal details.

---

# 12. API Security Rules

1. Public endpoints must return only publicly accessible information.
2. Protected endpoints require valid authentication.
3. Report modifications require ownership or authorized privileges.
4. Claims must be accessible only to authorized users.
5. Exact private locations must not be exposed publicly.
6. Sensitive identity information must never appear in ordinary search responses.
7. Uploaded files must be validated.
8. Matching scores cannot independently authorize recovery.
9. Pagination and rate limiting should protect listing endpoints.
10. Every permission must be enforced on the backend.

---

# 13. API Testing

Each endpoint should be tested for:

- Successful requests
- Missing required fields
- Invalid data types
- Authentication failures
- Authorization failures
- Missing resources
- Duplicate submissions
- Invalid state transitions
- Database failures where applicable

Recommended tools:

- Postman
- Swagger UI / OpenAPI
- JUnit
- MockMvc
- Spring Boot integration tests

---

# 14. Implementation Status

| Module              | Status                            |
| ------------------- | --------------------------------- |
| Authentication      | Existing                          |
| User profile        | Existing                          |
| Item CRUD           | Existing                          |
| Claims              | Existing                          |
| Image functionality | Existing; API verification needed |
| Notifications       | Existing; API verification needed |
| Unified search      | Planned                           |
| Geographic search   | Planned                           |
| SmartMatch          | Planned                           |
| Recovery management | Planned                           |

The actual source code is the authority for implemented endpoint paths, field names, response schemas, and status codes.

## Related Documentation

- `02-USER-FLOWS.md` — User journeys
- `03-ARCHITECTURE.md` — System architecture
- `04-DATABASE.md` — Database schema
- `06-CODEBASE.md` — Classes and file responsibilities
- `07-SMARTMATCH.md` — Matching algorithm
- `08-SECURITY.md` — Authentication and authorization

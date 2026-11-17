# Lost and Found

A REST API for reporting lost or found items and helping users arrange their return. Users can register, publish item reports, and submit claims against other users' reports.

## Technology

- Java 21 and Spring Boot (version defined in `pom.xml`)
- Spring Web MVC, Spring Data JPA, and Jakarta Validation
- MySQL
- Spring Security with JWT authentication and PBKDF2 password hashing
- Maven Wrapper

## Run locally

1. Install Java 21 and MySQL, and create a database named `lostfound`.
2. Set the database connection and a Base64-encoded JWT secret containing at least 32 random bytes:

   ```bash
   export DB_URL='jdbc:mysql://localhost:3306/lostfound'
   export DB_USERNAME='your_database_user'
   export DB_PASSWORD='your_database_password'
   export APP_JWT_SECRET="$(openssl rand -base64 32)"
   ```

3. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

The default address is `http://localhost:8080`. Set `PORT` to use a different port. Hibernate updates the database schema automatically through `spring.jpa.hibernate.ddl-auto=update`.

## Users and authentication

Register with `name`, `email`, and `password`, then sign in with `email` and `password`. Email addresses are trimmed and converted to lowercase; duplicate registrations return `409 Conflict`.

Sign-in returns `accessToken`, `tokenType`, and `expiresIn`. Tokens expire after one hour. Send the token on authenticated requests:

```http
Authorization: Bearer <accessToken>
```

## Item reports

An item report contains a title, description, type (`LOST` or `FOUND`), location, and event date. The event date must be today or earlier. The user who creates the report is its **reporter**; only that user can update or delete it.

Example request body for creating or updating an item:

```json
{
  "title": "Black wallet",
  "description": "Black leather wallet near the library entrance",
  "type": "FOUND",
  "location": "Central Library",
  "eventDate": "2026-11-16"
}
```

## Shared claim workflow for lost and found items

Both `LOST` and `FOUND` reports use the same claim model, endpoints, permissions, and statuses. There is no separate claim process for each type.

| Report type | Who submits the claim? | Who reviews it? |
| --- | --- | --- |
| `FOUND` | Someone who believes the found item belongs to them | The reporter who found the item |
| `LOST` | Someone who believes they have found the reported item | The reporter who lost the item |

1. A signed-in user submits the report's `itemId` and a description explaining the connection to the item.
2. The claim starts as `PENDING`.
3. The reporter reviews the claim and either approves or rejects it.
4. The claim becomes `APPROVED` or `REJECTED`.

Example claim request for either report type:

```json
{
  "itemId": 1,
  "description": "The wallet has my initials inside and contains a library card."
}
```

For a lost report, the description should instead explain what the claimant found and why it matches.

Claim rules:

- Users cannot submit claims against their own reports.
- A user can have only one pending claim per item; a duplicate returns `409 Conflict`.
- Only the reporter can approve or reject a claim; other users receive `403 Forbidden`.
- A processed claim cannot be approved or rejected again (`409 Conflict`).
- The personal claims endpoint returns claims submitted by the current user.

Approval currently changes only the claim status. It does not mark the item as resolved, arrange a handover, or automatically reject other claims.

## Current API

Paths below follow the controller mappings, including trailing slashes. All paths are relative to the application address.

| Method | Path | Purpose | Access |
| --- | --- | --- | --- |
| POST | `/api/auth/signup/` | Register a user | Public |
| POST | `/api/auth/signin/` | Obtain a JWT | Public |
| GET | `/api/user/me` | Get the current user's profile | Bearer token |
| GET | `/api/items/` | List all reports | Public |
| GET | `/api/items/{id}/` | Get one report | Public |
| POST | `/api/items/` | Create a report | Bearer token |
| PUT | `/api/items/{id}/` | Update a report | Reporter |
| DELETE | `/api/items/{id}/` | Delete a report | Reporter |
| POST | `/api/claims/` | Submit a claim for either item type | Bearer token |
| GET | `/api/claims/` | List claims submitted by the current user | Bearer token required by controller logic* |
| POST | `/api/claims/{id}/approve/` | Approve a pending claim | Reporter |
| POST | `/api/claims/{id}/reject/` | Reject a pending claim | Reporter |

*The security configuration currently permits anonymous GET requests to claim paths, but the controller requires a JWT to identify the user. This mismatch needs correction; clients should send a token.

## Notification system (planned; partially scaffolded)

The intended system provides in-app notifications for both lost and found reports using the same claim events.

| Event/type | Recipient | Notification purpose |
| --- | --- | --- |
| `CLAIM_SUBMITTED` | Reporter | A new claim needs review |
| `CLAIM_APPROVED` | Claimant | The reporter approved their claim |
| `CLAIM_REJECTED` | Claimant | The reporter rejected their claim |
| `ITEM_MATCHED` | Reporters of potentially matching reports | Optional future matching feature |
| `ITEM_RESOLVED` | Reporter and approved claimant | Optional future confirmation of return |

Each notification stores its recipient, type, title, message, related item ID, related claim ID, read status, and creation time. New notifications start unread. Item and claim references let the client open the related report or claim.

Proposed authenticated endpoints (not implemented):

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/notifications/` | List the current user's notifications, newest first |
| GET | `/api/notifications/unread-count/` | Get the current user's unread count |
| PATCH | `/api/notifications/{id}/read/` | Mark one of the current user's notifications as read |

The intended implementation creates claim notifications within the same transaction as the claim change. Recipients can only view or mark their own notifications. A client can poll the list and unread count; email, SMS, and real-time push delivery are outside this basic design.

Current implementation status: notification fields, event types, a repository, and a save service exist. However, `Notification` is missing its JPA `@Entity` annotation, the controller and response DTO are empty, and claim operations do not call the notification service. Notifications are not functional yet. Matching and item resolution also require additional application logic and, for resolution, an item status model.

## Code organization

All application code is under `src/main/java/com/learn/lostfound`:

| Package | Responsibility |
| --- | --- |
| `controller` | HTTP routes and request handling |
| `service` | Business rules and transactions |
| `repository` | Database access through Spring Data JPA |
| `entity` | User, item, claim, and notification data models |
| `dto` | Request and response structures |
| `enums` | Item types, claim statuses, and notification types |
| `config` | Security, JWT, and password configuration |

A user can create many item reports and claims. Each claim belongs to one item and one claimant. Each notification belongs to one recipient.

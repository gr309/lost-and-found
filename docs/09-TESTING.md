
# Testing & Quality Assurance

**Project:** Lost & Found Bangladesh
**Backend:** Spring Boot
**Testing Frameworks:** JUnit 5, Mockito, Spring Boot Test
**Version:** 1.0
**Status:** Development

## 1. Purpose

Ensure that the application is reliable, secure, and functions correctly before deployment.

Testing covers business logic, APIs, database operations, authentication, authorization, and intelligent matching.

## 2. Testing Strategy

| Test Type   | Purpose                                        | Tools                |
| ----------- | ---------------------------------------------- | -------------------- |
| Unit        | Validate individual methods and business rules | JUnit, Mockito       |
| Integration | Verify components and database interactions    | Spring Boot Test     |
| API         | Verify endpoints, JSON, and HTTP responses     | MockMvc, Postman     |
| Security    | Verify authentication and authorization        | Spring Security Test |
| End-to-End  | Validate complete user workflows               | Playwright           |
| Performance | Evaluate response times and concurrency        | k6 / JMeter          |

## 3. Test Directory Structure

Recommended structure:

```text
src/test/java/com/learn/lostfound/
│
├── controller/
│   ├── AuthControllerTest.java
│   ├── ItemControllerTest.java
│   └── ClaimControllerTest.java
│
├── service/
│   ├── ItemServiceTest.java
│   ├── ClaimServiceTest.java
│   └── NotificationServiceTest.java
│
├── repository/
│   └── ItemRepositoryTest.java
│
├── security/
│   └── SecurityIntegrationTest.java
│
├── matching/                    # Planned
│   ├── MatchingServiceTest.java
│   └── SimilarityScoringTest.java
│
└── integration/
    └── RecoveryFlowTest.java    # Planned
```

These are proposed test files, not confirmed existing files.

## 4. Unit Testing

Unit tests validate individual business operations without requiring the complete application.

### Important Test Cases

| Component           | Test                              |
| ------------------- | --------------------------------- |
| ItemService         | Create valid report               |
| ItemService         | Reject invalid report             |
| ItemService         | Prevent unauthorized modification |
| ClaimService        | Submit valid claim                |
| ClaimService        | Reject duplicate claims           |
| ClaimService        | Approve authorized claim          |
| ClaimService        | Reject unauthorized approval      |
| NotificationService | Create correct notification       |
| MatchingService     | Calculate correct score           |
| LocationService     | Calculate geographic distance     |

External dependencies should be mocked where appropriate.

## 5. API Testing

Each API must be tested for:

- Valid request
- Invalid request body
- Missing required fields
- Invalid authentication
- Insufficient permission
- Resource not found
- Invalid state transition
- Expected HTTP response

### Example: Item Creation

**Endpoint:** `POST /api/items/`

| Scenario                    | Expected Result  |
| --------------------------- | ---------------- |
| Valid authenticated request | 201 Created      |
| Missing JWT                 | 401 Unauthorized |
| Missing title               | 400 Bad Request  |
| Invalid item type           | 400 Bad Request  |
| Invalid date                | 400 Bad Request  |

The exact expected codes must follow the implemented API contract.

## 6. Integration Testing

Integration tests verify interactions between controllers, services, repositories, and the database.

### Required Scenarios

1. Register user → Login → Access protected endpoint.
2. Create item → Save to database → Retrieve item.
3. Update item → Verify ownership → Save changes.
4. Submit claim → Save claim → Create notification.
5. Approve claim → Update status → Notify claimant.
6. Delete or close report → Enforce related business rules.
7. Create report → Trigger SmartMatch → Store candidate matches (planned).
8. Confirm recovery → Update relevant records (planned).

Use an isolated test database.

Testcontainers with MySQL is recommended for database integration tests.

## 7. Security Testing

### Authentication

- Reject requests without required authentication.
- Reject invalid JWT.
- Reject expired JWT.
- Verify correct user identity.

### Authorization

- User A cannot modify User B's report.
- User A cannot access User B's private claims.
- Unauthorized users cannot approve claims.
- Users cannot access unrelated notifications.
- Non-admin users cannot access protected administrative endpoints.

### Input Security

- Reject invalid file uploads.
- Validate search parameters.
- Prevent unsafe query construction.
- Verify rate limiting where implemented.

## 8. SmartMatch Testing — Planned

SmartMatch must be tested using both deterministic test cases and labeled evaluation data.

### Unit Test Cases

| Scenario                  | Expected Behavior                    |
| ------------------------- | ------------------------------------ |
| Identical item attributes | High similarity                      |
| Different item categories | Reduced similarity                   |
| Nearby locations          | Higher location score                |
| Distant locations         | Lower location score                 |
| Similar descriptions      | Positive text similarity             |
| Missing attributes        | Safe score normalization             |
| LOST vs LOST              | No candidate match                   |
| Duplicate pair            | No duplicate record                  |
| Resolved report           | Excluded according to matching rules |

### Evaluation Metrics

**Precision**

```text
Precision = TP / (TP + FP)
```

**Recall**

```text
Recall = TP / (TP + FN)
```

**F1-score**

```text
F1 = 2 × Precision × Recall / (Precision + Recall)
```

Where:

- TP = True Positive
- FP = False Positive
- FN = False Negative

Also evaluate:

- Precision@K
- Recall@K
- False notification rate
- Matching execution time

Handle zero denominators explicitly.

### Matching Dataset

Create a labeled dataset containing:

- True lost/found pairs
- Similar but unrelated items
- Different descriptions of the same item
- Missing information
- Approximate locations
- Bangla and English descriptions

Do not use private user reports for evaluation without appropriate authorization and safeguards.

## 9. End-to-End Testing

Validate complete user journeys.

### Flow 1: Report Lost Item

```text
Login
  ↓
Create Lost Report
  ↓
Submit
  ↓
Verify Confirmation
  ↓
Open My Reports
  ↓
Verify Report Exists
```

### Flow 2: Ownership Claim

```text
Login
  ↓
Open Found Item
  ↓
Submit Claim
  ↓
Report Owner Reviews Claim
  ↓
Approve / Reject
  ↓
Verify Notification
```

### Flow 3: SmartMatch — Planned

```text
Create Lost Report
       ↓
Create Matching Found Report
       ↓
Run Matching
       ↓
Verify Match Record
       ↓
Verify Notification
```

## 10. Performance Testing

Measure:

| Metric              | Purpose              |
| ------------------- | -------------------- |
| API response time   | Request performance  |
| Search latency      | Search efficiency    |
| Matching duration   | Matching performance |
| Database query time | Query optimization   |
| Concurrent requests | Load handling        |
| Error rate          | Reliability          |

Performance targets should be established using real measurements rather than assumptions.

## 11. Running Tests

From the backend project root:

```bash
./mvnw test
```

Run a specific test:

```bash
./mvnw -Dtest=ItemServiceTest test
```

Run Maven verification:

```bash
./mvnw verify
```

On systems without the Maven wrapper:

```bash
mvn test
```

Integration tests may require additional database configuration.

## 12. Test Environment

Tests must not modify production data.

Use separate environment configurations:

```text
Development → Local database
Testing     → Isolated test database
Production  → Production database
```

Test credentials and sample data must not contain real user secrets.

## 13. CI/CD Quality Checks

Recommended pipeline:

```text
Code Push
    ↓
Compile
    ↓
Unit Tests
    ↓
Integration Tests
    ↓
Security Checks
    ↓
Build
    ↓
Deployment Approval
```

Failed required checks should prevent production deployment.

## 14. Release Checklist

- [ ] Application builds successfully
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] API contracts verified
- [ ] Authentication tests pass
- [ ] Authorization tests pass
- [ ] Database migrations tested
- [ ] Critical user flows verified
- [ ] No exposed credentials
- [ ] Production configuration reviewed
- [ ] Documentation updated

## 15. Related Documentation

- `05-API.md` — API contracts
- `06-CODEBASE.md` — Classes and methods
- `07-SMARTMATCH.md` — Matching algorithm
- `08-SECURITY.md` — Security requirements
- `10-DEPLOYMENT.md` — Deployment and release

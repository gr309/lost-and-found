
# User Flows

**Project:** Lost & Found Bangladesh
**Version:** 1.0
**Status:** Development

## 1. User Roles

| Role      | Access                                            |
| --------- | ------------------------------------------------- |
| Guest     | Browse and search public reports                  |
| User      | Report items, submit claims, manage recovery      |
| Moderator | Review reports, claims, and disputes              |
| Admin     | Manage users, moderation, and platform operations |

## 2. Registration and Login

**Flow:**

```text
User → Registration/Login Form
     → Authentication API
     → AuthController
     → Authentication Service
     → UserRepository
     → Database
     → JWT Response
     → Frontend
```

**Success:** User receives authentication credentials and can access protected features.

**Failure:** Invalid credentials, duplicate email, invalid input, or unauthorized access.

## 3. Report a Lost Item

**Flow:**

```text
User → Report Lost Item
     → Enter Item Details
     → Select Location on Map
     → Upload Image (Optional)
     → Submit Report
     → ItemController
     → ItemService
     → ItemRepository
     → Database
     → Trigger SmartMatch
     → Show Confirmation
```

**Success:** Report is saved and becomes eligible for searching and matching.

**Failure:** Missing required information, invalid location, unauthorized request, or upload failure.

## 4. Report a Found Item

**Flow:**

```text
Finder → Report Found Item
       → Enter Description
       → Select Found Location
       → Upload Image (Optional)
       → Submit
       → ItemController
       → ItemService
       → ItemRepository
       → Database
       → Trigger SmartMatch
```

**Success:** Found report becomes available for eligible searches.

**Failure:** Invalid information, unauthorized request, or storage failure.

**Privacy:** Sensitive identifying details must not be publicly exposed.

## 5. Unified Search

**Flow:**

```text
User → Search Bar / Filters
     → Search API
     → SearchController
     → SearchService
     → ItemRepository
     → Database
     → Ranked Results
     → Frontend
```

**Supported filters:**

- Item name and description
- Category and attributes
- Lost/found type
- Location and search radius
- Date and approximate time

**Success:** Relevant public reports appear.

**Failure:** Invalid filters produce validation errors; no matches produce an empty result.

## 6. Map-Based Search

**Flow:**

```text
User → Open Map
     → Select Location
     → Set Search Radius
     → Nearby Search API
     → LocationSearchService
     → Geographic Database Query
     → Nearby Reports
     → Display Map Markers
```

**Success:** Reports within the requested geographical area appear.

**Failure:** Invalid coordinates, unavailable map service, or no nearby reports.

**Privacy:** Display approximate public locations when exact coordinates are sensitive.

## 7. Automatic SmartMatch

**Flow:**

```text
New/Updated Report
       ↓
Matching Trigger
       ↓
MatchingService
       ↓
Retrieve Opposite-Type Reports
       ↓
Compare Text, Category, Attributes,
Location, and Time
       ↓
Calculate Match Score
       ↓
Save Potential Matches
       ↓
Notify Relevant Users
```

**Success:** Potential matches are stored and eligible users are notified.

**Failure:** Matching errors are logged and eligible tasks may be retried.

**Rule:** Similarity scores do not establish ownership.

## 8. Ownership Claim

**Flow:**

```text
User → View Found Item
     → Submit Ownership Claim
     → ClaimController
     → ClaimService
     → Validate Claim
     → ClaimRepository
     → Database
     → Notify Report Owner
```

**Success:** Claim is submitted with `PENDING` status.

**Failure:** Unauthorized request, invalid evidence, duplicate claim, or ineligible item.

## 9. Claim Verification

**Flow:**

```text
Finder / Authorized Reviewer
       ↓
View Submitted Claim
       ↓
Review Private Ownership Evidence
       ↓
Approve / Reject
       ↓
ClaimController
       ↓
ClaimService
       ↓
Update Claim Status
       ↓
Notify Claimant
```

**Success:** Claim becomes `VERIFIED` or `REJECTED`.

**Failure:** Insufficient permission, invalid status transition, or unavailable evidence.

## 10. Item Recovery

**Flow:**

```text
Verified Claim
      ↓
Arrange Safe Handover
      ↓
Transfer Item
      ↓
Confirm Handover
      ↓
RecoveryService
      ↓
Update Recovery Status
      ↓
Mark Report Resolved
      ↓
Notify Participants
```

**Success:** Recovery is confirmed and recorded.

**Failure:** Cancelled handover, disputed ownership, or incomplete confirmation.

## 11. Notifications

**Flow:**

```text
System Event
     ↓
NotificationService
     ↓
NotificationRepository
     ↓
Database
     ↓
User Notification List
```

**Events:**

- Potential match found
- Claim submitted
- Claim approved/rejected
- Handover requested
- Recovery completed

Users can view notifications and mark them as read.

## 12. Report Management

**Flow:**

```text
User → My Reports
     → Select Report
     → Update / Close / Delete
     → ItemController
     → ItemService
     → Verify Ownership
     → Update Database
```

**Success:** Authorized changes are saved.

**Failure:** Unauthorized modification, invalid data, or prohibited status change.

## 13. Administrative Moderation

**Flow:**

```text
Moderator/Admin
      ↓
Review Flagged Content
      ↓
ModerationController
      ↓
ModerationService
      ↓
Apply Authorized Action
      ↓
Update Database
      ↓
Record Audit Log
```

**Actions:** Review, approve, restrict, or remove reports according to platform policies.

## 14. General API Error Handling

| HTTP Status | Meaning                                      |
| ----------- | -------------------------------------------- |
| 400         | Invalid request                              |
| 401         | Authentication required or invalid           |
| 403         | Insufficient permission                      |
| 404         | Resource not found                           |
| 409         | Business rule or state conflict              |
| 422         | Semantically invalid input, where applicable |
| 429         | Too many requests                            |
| 500         | Unexpected server error                      |

Errors should use a consistent JSON response format.

## 15. Implementation Notes

- Existing authentication, item, claim, and notification flows must be verified against the actual Spring Boot source.
- SmartMatch, geographic search, moderation, and recovery flows represent target functionality until implemented.
- Controller and service names shown here are conceptual where not verified.
- Detailed JSON contracts belong in `05-API.md`.
- Exact class responsibilities belong in `06-CODEBASE.md`.
- Every critical workflow should have automated integration tests.

# Lost & Found Bangladesh — Engineering Documentation

**Version:** 1.0 (engineering blueprint)**Updated:** 2026-11-21**Target:** Nationwide lost-and-found and verified recovery platform**Backend:** Java, Spring Boot, Spring Security, Spring Data JPA, MySQL**Frontend:** React**Status:** Existing MVP plus proposed nationwide modules

> **Evidence convention:** `EXISTING / REPORTED` means observed in previous project discussions, but not revalidated from the current Git commit. `PROPOSED` means a design contract or file that must be implemented. `VERIFY` means inspect the repository before calling the description definitive. This is **not** an automatically generated source-code audit. All API samples are examples unless explicitly confirmed.

## 1. Documentation map

| Area             | Contents                                                                        | Intended reader               |
| ---------------- | ------------------------------------------------------------------------------- | ----------------------------- |
| Product          | Purpose, actors, scope and rules                                                | Stakeholder, product manager  |
| Flows            | User journeys, alternate/error paths                                            | Product, QA, frontend/backend |
| API contracts    | HTTP method, URL, authorization, JSON request/response, errors                  | Frontend/backend              |
| Execution traces | Frontend → filter → controller → service → repository → database → events | Developers                    |
| File map         | Class ownership and relationships                                               | Developers                    |
| Data             | Entities, constraints, indexing, state machines                                 | Backend, DBA                  |
| Operations       | Environment, deployment, monitoring, backup                                     | DevOps                        |
| Verification     | Tests, acceptance scenarios, release checklist                                  | QA                            |

## 2. Product scope and terminology

**Goal:** Help people throughout Bangladesh discover and recover belongings by combining reports, proximity and time-aware search, candidate matching, notifications, private ownership verification and safe handovers.

**Key terminology:** `Report` = a LOST or FOUND listing. `Candidate match` = a potentially related pair of opposite-type reports, not proof of ownership. `Claim` = a person's assertion of ownership of a found item. `Verification` = evidence-based decision on a claim. `Recovery` = physical handover recorded as completed. `Event time` = when loss/discovery happened; `createdAt` = when report was submitted. `Location precision` = how certain a report's point/area is.

**Actors:** Visitor (browse public-safe data), User (report lost/found and claim), Reporter/Finder (maintain their own reports, review allowed claims), Moderator (review disputes and abuse), Admin (manage system), Partner Institution (future: verified handover site).

**Non-goals at launch:** Payments and rewards, continuous movement tracking, public full IDs/documents, automatically declaring ownership from match scores, guaranteed recovery, route navigation.

## 3. Existing system baseline (verify against `develop`)

Previous implementation discussions identify the following **existing/reported** API surface:

| Method | URL                           | Auth     | Existing/reported intention |
| ------ | ----------------------------- | -------- | --------------------------- |
| POST   | `/api/auth/signup/`         | Public   | Register user               |
| POST   | `/api/auth/signin/`         | Public   | Get JWT                     |
| GET    | `/api/user/me/`             | JWT      | Current user's profile      |
| GET    | `/api/items/`               | Public   | List reports                |
| GET    | `/api/items/{id}/`          | Public   | Item details                |
| POST   | `/api/items/`               | JWT      | Create report               |
| PUT    | `/api/items/{id}/`          | Reporter | Update own report           |
| DELETE | `/api/items/{id}/`          | Reporter | Delete own report           |
| POST   | `/api/claims/`              | JWT      | Submit ownership claim      |
| GET    | `/api/claims/`              | JWT      | List current user's claims  |
| POST   | `/api/claims/{id}/approve/` | Reporter | Approve claim               |
| POST   | `/api/claims/{id}/reject/`  | Reporter | Reject claim                |

**Known package prefix (from prior project source excerpts):** `com.learn.lostfound`.

**Previously discussed Java classes (VERIFY actual paths and signatures):** `SecurityConfig`, `ClaimService`, `ClaimRepository`, `ItemRepository`, `UserRepository`, `NotificationService`, `ClaimRequest`, `ClaimResponse`, `Claim`, `Item`, `User`, enums `ClaimStatus`, `ItemType`, `NotificationType`. Do not assume other names in this document currently exist.

**Important compatibility rule:** Before introducing request fields or routes, capture the live OpenAPI contract or controller signatures. Old clients must continue to work, or a documented `/api/v2/` transition must be introduced.

## 4. Target state — feature/modules matrix

| Module       | User capability                | Internal responsibility                   | Stage                               |
| ------------ | ------------------------------ | ----------------------------------------- | ----------------------------------- |
| Auth         | Register/login                 | Hash passwords, issue/validate tokens     | EXISTING / REPORTED                 |
| Item         | Report/manage item             | CRUD, ownership, safe public DTO          | EXISTING / REPORTED                 |
| Claim        | Submit/review claim            | Claim authorization and transitions       | EXISTING / REPORTED                 |
| Notification | Read alerts                    | Store and deliver user events             | EXISTING / REPORTED (verify routes) |
| Location     | Pin/area, approximate location | Normalize geography, preserve uncertainty | PROPOSED                            |
| Search       | Keyword, filter, map, radius   | Candidate queries, pagination, ranking    | PROPOSED                            |
| Matching     | Suggested pairs                | Retrieve candidates, score, dedupe        | PROPOSED                            |
| Verification | Private ownership evidence     | Confidential review and audit             | PROPOSED                            |
| Recovery     | Arrange/confirm handover       | State machine and consent                 | PROPOSED                            |
| Moderation   | Report fraud/abuse             | Restrict, investigate and appeal          | PROPOSED                            |
| Analytics    | Recovery statistics            | Aggregate non-sensitive metrics           | PROPOSED                            |
| JourneyMatch | Along-route candidate search   | Route corridor and time-aware ranking     | FUTURE                              |

## 5. User journeys with end-to-end execution

### UF-01 — User registers and signs in

**Preconditions:** No account is required; email is not already used.
**Happy path:** Open signup → submit valid details → API validates uniqueness → password hashed → user stored → login returns JWT → client stores session safely → profile loads.
**Alternative paths:** Duplicate email `409` or validation `400/422`; wrong credentials `401`; disabled user `403`.
**Trace (existing class names partly inferred, VERIFY):**

```text
SignupPage.jsx [PROPOSED frontend filename]
  → authApi.signup() [PROPOSED]
  → POST /api/auth/signup/
  → Spring Security filter chain (public endpoint)
  → AuthController [VERIFY]
  → AuthService [VERIFY]
  → UserRepository [EXISTING / REPORTED]
  → users table
  → response DTO
```

```text
SignInPage.jsx → POST /api/auth/signin/
  → AuthController [VERIFY] → authentication provider [VERIFY]
  → UserRepository → password verification
  → JWT provider [VERIFY] → access token response
```

### UF-02 — Report a lost item (with map)

**Actor:** Authenticated user. **Goal:** Make a searchable LOST report.
**Happy path:** Click “Report Lost” → provide title/category/description/time → search address or drop a pin → set accuracy/uncertainty → optionally upload public-safe photo → review privacy → submit → item stored → matching event scheduled → result screen.
**Alternative paths:** Unknown exact point → select approximate area; storage error → report should not refer to a nonexistent photo; invalid time → validation error; duplicate submission → idempotent client retry or duplicate detection.

```text
ReportItemPage.jsx [PROPOSED]
 → MapLocationPicker.jsx [PROPOSED]
 → itemApi.createItem() [PROPOSED]
 → POST /api/items/ [EXISTING route, payload extension PROPOSED]
 → JWT authentication filter [VERIFY]
 → ItemController [VERIFY]
 → ItemService [VERIFY]
 → ItemRepository [EXISTING / REPORTED]
 → items + item_location (+ item_images) [location table PROPOSED]
 → after-commit ItemReportedEvent [PROPOSED]
 → MatchingWorker [PROPOSED]
 → Item response
```

**Postcondition:** Report is `ACTIVE`; it is discoverable according to visibility settings; matching is queued independently of HTTP response.

### UF-03 — Finder posts an item

**Actor:** Authenticated finder. **Goal:** Minimize effort to report.
**Happy path:** Choose “Found” → photograph/short description + found area/date → hide identifiers from public picture → submit → receive reference ID → automatic search for LOST candidates.
**Safety:** Public API never exposes private evidence/visible ID numbers or exact sensitive pickup coordinates.
**Trace:** same controller/service/repository as UF-02; `type=FOUND`; opposite-type match candidates are LOST reports.

### UF-04 — User searches by text, time and radius

**Actor:** Any visitor; authentication only required for private actions.
**Happy path:** Enter “black wallet near Shaheb Bazar” → select center coordinate/area, radius, date range, type `FOUND` → backend validates filter limits → obtains candidates from database → ranks → paginated public-safe results returned → map clusters markers.
**Alternative paths:** Unknown place → suggest corrected locality; empty results → suggest wider radius/nationwide; invalid radius → `400`; too many results → bounded pagination.

```text
SearchPage.jsx / MapExplorer.jsx [PROPOSED]
 → searchApi.search() [PROPOSED]
 → GET /api/search/items/ [PROPOSED]
 → SearchController [PROPOSED]
 → SearchService [PROPOSED]
 → SearchSpecification or SearchRepository [PROPOSED]
 → indexed items + item_location
 → SearchRankingService [PROPOSED]
 → public SearchResult DTO
```

### UF-05 — Auto-match when a report arrives

**Trigger:** An item has committed successfully.
**Flow:** `ItemReportedEvent` → select opposite-type active reports → broad candidate retrieval → compute category/text/attribute/location/time similarity → save unique pairs and explanation (with algorithm version) → notify relevant users only when notification policy allows.
**Alternative paths:** No candidates → leave ACTIVE; downstream failure → retry safely; duplicate event → upsert same pair, no duplicate push; existing closed match → do not notify again.
**Important:** `89%` is an algorithmic similarity index, **not** 89% ownership probability.

```text
ItemService → DB commit → ItemReportedEvent
 → MatchingWorker
 → CandidateRetrievalService → ItemRepository / SearchRepository
 → MatchScoringService
 → ItemMatchRepository → item_matches + match_evidence
 → NotificationService [EXISTING / REPORTED]
 → notifications
```

### UF-06 — Claim and verify a found object

**Actor:** Potential owner.
**Flow:** Opens FOUND item → submits **private** distinguishing detail → `ClaimService` validates claim and authorized target → stores claim → finder/moderator reviews restricted evidence → approves or rejects → notification emitted.
**Alternative paths:** User claims own item → reject; multiple competing claims → review; public info alone used as proof → insufficient; claim withdrawn → audited.
**Trace:**

```text
ItemDetailPage.jsx [PROPOSED]
 → claimApi.submitClaim() [PROPOSED]
 → POST /api/claims/ [EXISTING / REPORTED]
 → ClaimController [VERIFY]
 → ClaimService [EXISTING / REPORTED]
 → ClaimRepository + ItemRepository + UserRepository [EXISTING / REPORTED]
 → claims table
 → NotificationService [EXISTING / REPORTED]
```

### UF-07 — Safe handover and close recovery

**Flow:** Verified claim → propose public/partner handover location → both parties consent → item transferred → parties confirm (or moderator resolves dispute) → handover `COMPLETED` → item `RESOLVED` → associated search/match notifications suppressed.
**Alternative paths:** No-show, dispute, expired arrangement, false confirmation; no automatic finalization from match score or single claimant click.
**Trace:** RecoveryController → RecoveryService → ClaimRepository/RecoveryRepository/ItemRepository → state transition and audit log → notification event. **All recovery classes/routes PROPOSED.**

## 6. API conventions and security

- **Base:** `/api`; existing routes retained as listed in §3.
- **Authentication:** `Authorization: Bearer <JWT>` for protected operations.
- **Content type:** `application/json` except multipart upload.
- **Date/time:** ISO 8601; event time local with `Asia/Dhaka` or timezone offset; store canonical instants in UTC when applicable.
- **Pagination:** proposed `page=0&size=20`, maximum size 100; define consistent ordering and total count.
- **Error envelope (PROPOSED):** `timestamp`, `status`, `code`, `message`, `path`, `fieldErrors`, `traceId`.
- **Statuses:** `200` read/update, `201` create, `204` delete where applicable, `400` invalid request, `401` unauthenticated, `403` unauthorized, `404` not found/private resource, `409` conflict, `422` domain validation, `429` rate limit, `500` internal failure.
- **Authorization:** Every item/claim/match ID is re-validated on backend; client-side checks are convenience only.

### API-01 — Signup (existing/reported route; example payload)

```http
POST /api/auth/signup/
Content-Type: application/json
```

```json
{"name":"Example User","email":"user@example.com","password":"REPLACE_WITH_STRONG_PASSWORD"}
```

**Example success — `201` (verify actual response):**

```json
{"id":42,"name":"Example User","email":"user@example.com"}
```

**Trace:** request → security permitAll → AuthController [VERIFY] → AuthService [VERIFY] → UserRepository → DB. **Failure:** duplicate account (`409`); invalid fields (`400/422`). Never echo password.

### API-02 — Signin (existing/reported route; example payload)

```http
POST /api/auth/signin/
Content-Type: application/json
```

```json
{"email":"user@example.com","password":"REPLACE_WITH_PASSWORD"}
```

**Example success `200` (verify actual field names):**

```json
{"accessToken":"<jwt>","tokenType":"Bearer","expiresIn":3600}
```

**Trace:** auth controller → authentication service → password hash check → token generation. **Errors:** `401` bad credentials, `403` disabled account.

### API-03 — Create item (existing/reported route; PROPOSED nationwide payload)

```http
POST /api/items/
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "type":"LOST",
  "title":"Black leather wallet",
  "categoryId":12,
  "description":"Lost during afternoon travel",
  "eventDate":"2026-11-20",
  "timeFrom":"14:00:00",
  "timeTo":"16:00:00",
  "attributes":{"primaryColor":"BLACK","material":"LEATHER"},
  "location":{"latitude":24.3705,"longitude":88.6042,"label":"Shaheb Bazar, Rajshahi","precision":"APPROXIMATE","uncertaintyRadiusMeters":300},
  "imageIds":[101]
}
```

**Example success — `201`:**

```json
{"id":567,"type":"LOST","title":"Black leather wallet","status":"ACTIVE","matchProcessingStatus":"PENDING","createdAt":"2026-11-21T16:00:00Z"}
```

**Trace:** ItemController [VERIFY] → DTO validation → ItemService [VERIFY] → ItemRepository → `items` and proposed `item_locations` → event → matching worker. **Errors:** `400` missing title, `422` invalid coordinates/date or image ownership, `401` no token, `413` if upload too large. **Compatibility:** Existing client JSON may use different field names; do not replace without verifying source.

### API-04 — List and details (existing/reported routes)

```http
GET /api/items/?type=FOUND
GET /api/items/567/
```

**Example safe response `200`:**

```json
[{"id":567,"title":"Dark wallet","type":"FOUND","location":"Rajshahi","eventDate":"2026-11-20","createdAt":"2026-11-21T16:00:00Z"}]
```

**Trace:** ItemController → ItemService → ItemRepository → public DTO. **Security:** No hidden verification clues, document numbers or precise private coordinates.

### API-05 — Unified search (PROPOSED)

```http
GET /api/search/items/?q=wallet&type=FOUND&district=Rajshahi&lat=24.3705&lng=88.6042&radiusMeters=5000&from=2026-11-18&to=2026-11-21&page=0&size=20
```

**Example response `200`:**

```json
{
  "items":[{"id":780,"title":"Dark leather wallet","type":"FOUND","locationLabel":"Rajshahi","distanceMeters":650,"relevanceScore":86.2}],
  "page":0,"size":20,"totalElements":1,"totalPages":1
}
```

**Trace:** SearchController → SearchService → validated SearchCriteria → SearchRepository/SQL spatial filter → ranking → safe DTO. **Errors:** `400` invalid range, missing one of latitude/longitude, radius exceeds server limit. **Privacy:** coordinates used internally do not imply exact coordinates sent to client.

### API-06 — Nearby reports (PROPOSED)

```http
GET /api/search/nearby/?lat=24.3705&lng=88.6042&radiusMeters=3000&type=FOUND
```

**Example response `200`:**

```json
{"center":{"lat":24.3705,"lng":88.6042},"radiusMeters":3000,"items":[{"id":780,"title":"Dark wallet","approxDistanceMeters":650,"publicLocation":{"label":"Rajshahi","lat":24.37,"lng":88.60}}]}
```

**Trace:** SearchController → geospatial query → public DTO. **Note:** use indexed candidate narrowing then `ST_Distance_Sphere`; route distance is not the same as straight-line distance.

### API-07 — My matches (PROPOSED)

```http
GET /api/matches/my/?itemId=567
Authorization: Bearer <jwt>
```

**Example response `200`:**

```json
{"matches":[{"id":901,"lostItemId":567,"foundItemId":780,"score":89.5,"classification":"VERY_STRONG","explanation":{"category":"Same category","location":"Nearby","time":"Compatible"},"algorithmVersion":"rules-v1"}]}
```

**Trace:** MatchingController → MatchingQueryService → ItemMatchRepository → ownership check → redacted match DTO. **Errors:** `401`, `403/404` for unowned item, `400` invalid filter.

### API-08 — Submit claim (existing/reported route; example payload)

```http
POST /api/claims/
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{"itemId":780,"evidenceDescription":"There is a small stitched mark inside the wallet","privateAnswers":{"distinctiveFeature":"Blue lining"}}
```

**Example success `201` (VERIFY actual response/status):**

```json
{"id":310,"itemId":780,"status":"PENDING","createdAt":"2026-11-21T16:05:00Z"}
```

**Trace:** ClaimController [VERIFY] → ClaimService [EXISTING / REPORTED] → ClaimRepository/ItemRepository/UserRepository → notification. **Security:** Submit against FOUND reports only as defined by chosen business policy; evidence visible only to authorized reviewer, never item listing.

### API-09 — Approve/reject claim (existing/reported routes)

```http
POST /api/claims/310/approve/
Authorization: Bearer <reporter-jwt>
```

**Example response `200` (VERIFY):**

```json
{"id":310,"status":"APPROVED"}
```

**Trace:** ClaimController → ClaimService → verify reporter owns target item → allowed transition → ClaimRepository → NotificationService. **Errors:** `403` non-owner, `404` not found, `409` already rejected/closed. Note that existing `APPROVED` may need to map to the future `VERIFIED` state; migrate deliberately, not by blind enum rename.

### API-10 — Confirm handover (PROPOSED)

```http
POST /api/recoveries/66/confirm/
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{"confirmation":"RECEIVED","confirmationCode":"<one-time-code>"}
```

**Example response `200`:**

```json
{"recoveryId":66,"status":"AWAITING_OTHER_PARTY","confirmedByCurrentUser":true}
```

**Trace:** RecoveryController → RecoveryService → transactionally update confirmation + audit log → finalize only on verification policy → NotificationService. **Errors:** `403` not a party, `409` invalid transition, `422` invalid code.

### API-11 — Error envelope (PROPOSED unified form)

```json
{
  "timestamp":"2026-11-21T16:10:00Z",
  "status":422,
  "code":"INVALID_LOCATION",
  "message":"Location coordinates are outside supported bounds",
  "path":"/api/items/",
  "fieldErrors":{"location.latitude":"Invalid latitude"},
  "traceId":"req_abc123"
}
```

Do not expose database exception details, stack traces, SQL, credentials, or private user data.

## 7. File-level responsibility map

**Names below are a recommended target layout, NOT proof that files exist in the current repository.** Names marked `KNOWN` appeared in prior project source excerpts. Every new class should have one clear reason to exist.

```text
src/main/java/com/learn/lostfound/
├── LostfoundApplication.java                [VERIFY] application bootstrap
├── config/
│   ├── SecurityConfig.java                  [KNOWN] Spring Security route/filter config
│   ├── CorsConfig.java                      [PROPOSED] origins if not in SecurityConfig
│   └── OpenApiConfig.java                   [PROPOSED] API documentation
├── controller/
│   ├── AuthController.java                  [VERIFY] /api/auth/** HTTP transport
│   ├── ItemController.java                  [VERIFY] /api/items/** transport
│   ├── ClaimController.java                 [VERIFY] /api/claims/** transport
│   ├── SearchController.java                [PROPOSED] search query endpoints
│   ├── MatchingController.java              [PROPOSED] read/dismiss match
│   ├── RecoveryController.java              [PROPOSED] handover transitions
│   └── NotificationController.java          [VERIFY] notification endpoints
├── service/
│   ├── AuthService.java                     [VERIFY] signup/signin rules
│   ├── ItemService.java                     [VERIFY] report lifecycle
│   ├── ClaimService.java                    [KNOWN] claim processing
│   ├── NotificationService.java             [KNOWN] notification storage/delivery
│   ├── SearchService.java                   [PROPOSED] filters and result ranking
│   ├── MatchScoringService.java             [PROPOSED] pure similarity math
│   ├── CandidateRetrievalService.java       [PROPOSED] fast candidate lookup
│   ├── MatchingWorker.java                  [PROPOSED] async orchestration and retry
│   ├── LocationService.java                 [PROPOSED] canonicalization/geospatial utils
│   ├── VerificationService.java             [PROPOSED] private evidence/review
│   └── RecoveryService.java                 [PROPOSED] safe handover workflow
├── repository/
│   ├── UserRepository.java                  [KNOWN] user persistence
│   ├── ItemRepository.java                  [KNOWN] item persistence
│   ├── ClaimRepository.java                 [KNOWN] claim persistence
│   ├── SearchRepository.java                [PROPOSED] filtered/spatial queries
│   ├── ItemMatchRepository.java             [PROPOSED] match persistence
│   └── RecoveryRepository.java              [PROPOSED] handover persistence
├── entity/
│   ├── User.java                            [KNOWN] account persistence model
│   ├── Item.java                            [KNOWN] lost/found persistence model
│   ├── Claim.java                           [KNOWN] claim persistence model
│   ├── ItemLocation.java                    [PROPOSED] geospatial details
│   ├── ItemMatch.java                       [PROPOSED] paired reports + ranking
│   ├── MatchEvidence.java                   [PROPOSED] score breakdown
│   └── ItemHandover.java                    [PROPOSED] confirmation/audit fields
├── dto/
│   ├── ClaimRequest.java                    [KNOWN] input DTO
│   ├── ClaimResponse.java                   [KNOWN] output DTO
│   ├── ItemCreateRequest.java               [PROPOSED] validated report input
│   ├── ItemPublicResponse.java              [PROPOSED] safe public report output
│   ├── SearchCriteria.java                  [PROPOSED] validated filters
│   ├── SearchResultResponse.java            [PROPOSED] paginated result
│   └── MatchResponse.java                   [PROPOSED] redacted match explanation
├── enums/
│   ├── ItemType.java                        [KNOWN] LOST/FOUND
│   ├── ClaimStatus.java                     [KNOWN] claim state
│   └── NotificationType.java                [KNOWN] event type
├── event/
│   └── ItemReportedEvent.java               [PROPOSED] matching trigger
├── exception/
│   └── GlobalExceptionHandler.java         [PROPOSED] stable errors
└── security/
    └── JwtAuthenticationFilter.java         [VERIFY] parses JWT, builds principal

src/main/resources/
├── application.properties                   [VERIFY] app config
└── db/migration/V*.sql                      [PROPOSED] schema migration scripts

frontend/src/ [VERIFY actual frontend repo layout]
├── pages/ReportItemPage.jsx                 [PROPOSED] report form
├── pages/SearchPage.jsx                     [PROPOSED] filter results
├── pages/MapExplorer.jsx                    [PROPOSED] geographic browsing
├── pages/ItemDetailPage.jsx                 [PROPOSED] item detail and claim
├── components/MapLocationPicker.jsx         [PROPOSED] map click/geocoder
├── components/MatchExplanation.jsx          [PROPOSED] safe score evidence
├── services/apiClient.js                    [PROPOSED] centralized HTTP/JWT handling
├── services/itemApi.js                      [PROPOSED] report endpoints
├── services/searchApi.js                    [PROPOSED] search endpoints
└── services/claimApi.js                     [PROPOSED] claim endpoints
```

### What each architectural layer should and should not do

| Layer/file type           | Does                                                    | Must avoid                                                     |
| ------------------------- | ------------------------------------------------------- | -------------------------------------------------------------- |
| `*Controller`           | Bind route, validate DTO, choose HTTP status            | Business rules, direct SQL                                     |
| `*Service`              | Authorization/business workflow, transaction boundaries | Returning JPA entities as public JSON                          |
| `*Repository`           | Queries and persistence                                 | HTTP handling, notifications                                   |
| `*Entity`               | Database mapping and constraints                        | HTTP request contracts                                         |
| `*Request` DTO          | Client input + validation                               | Trusting client-supplied `reporterId`, `score`, `status` |
| `*Response` DTO         | Explicit safe output fields                             | Sensitive evidence by default                                  |
| `SecurityConfig`/filter | Authenticate and restrict routes                        | Deciding claim ownership from untrusted payload                |
| `*Worker`               | Background/retryable matching                           | Sending duplicate notifications on retry                       |
| `*Event`                | Small immutable event payload                           | Carrying passwords/private documents                           |
| `*Mapper`               | Convert entity ↔ DTO                                   | Silently discarding important ownership checks                 |

## 8. Database and constraints

### Existing entities to inspect

`users`, `items`, `claims` and any `notifications` or item image tables: **VERIFY actual tables, columns, unique keys, and delete behavior from entities/migrations.**

### Proposed additions

```text
item_locations
  id PK; item_id FK UNIQUE; latitude DECIMAL(10,7); longitude DECIMAL(10,7)
  division_code; district_code; upazila_code; locality_label
  precision ENUM(POINT,APPROXIMATE,AREA); uncertainty_radius_meters

item_matches
  id PK; lost_item_id FK; found_item_id FK; score DECIMAL(5,2)
  status; algorithm_version; created_at; updated_at
  UNIQUE(lost_item_id, found_item_id)

match_evidence
  id PK; match_id FK; feature; weight; similarity; contribution

claim_verifications
  id PK; claim_id FK UNIQUE; status; review_method; reviewer_id; reviewed_at

item_handovers
  id PK; claim_id FK; status; agreed_location_private; consent flags; completed_at

abuse_reports
  id PK; reporter_id FK; target_type; target_id; reason; status; created_at

audit_logs
  id PK; actor_id FK; action; resource_type; resource_id; safe_metadata; created_at
```

**Constraints:** (1) match only opposite-type item pairs; (2) do not match item to itself; (3) match pair unique; (4) claims require eligibility and ownership rules; (5) location coordinate range checks; (6) avoid leaking private handover coordinates; (7) transactional status changes and audit.

**Indexes:** `items(type,status,event_date,category_id)`, `item_locations(item_id)`, suitable location spatial/bounding-box strategy, `item_matches(lost_item_id,score)`, `item_matches(found_item_id,score)`, `claims(item_id,status)`, `notifications(recipient_id,is_read,created_at)`. Validate with `EXPLAIN` on representative data. MySQL spatial choices depend on the actual schema/storage engine and SRID.

**Migration practice:** schema versions through Flyway or Liquibase; forward-only production migrations, tested rollback/restore procedures, backfill old location text to a nullable structured location where confidence is sufficient; do **not** fabricate coordinates for legacy reports.

## 9. State machines and business invariants

### Item

```text
ACTIVE → MATCHED → RESOLVED
   └──────────────→ CLOSED
ACTIVE/MATCHED → REMOVED (moderation)
```

`MATCHED` indicates candidates exist, **not** that ownership is confirmed.

### Claim

```text
PENDING → UNDER_REVIEW → VERIFIED → HANDOVER_PENDING → COMPLETED
                    └──→ REJECTED
PENDING/UNDER_REVIEW → WITHDRAWN
```

Existing implementation may currently use `APPROVED` instead of `VERIFIED`; record mapping and migration before any change. Do not permit conflicting transitions via concurrent requests.

### Recovery

```text
PROPOSED → ACCEPTED → SCHEDULED → AWAITING_CONFIRMATION → COMPLETED
         └── DECLINED     └── CANCELLED / DISPUTED
```

Only authorized parties may view a private handover, and only approved verification paths permit finalization.

## 10. SmartMatch scoring specification

1. **Candidate pool:** opposite type, active and public/eligible, exclude same user report self-match and duplicate pairs; broaden geography/time when necessary.
2. **Normalize:** lowercase, trim, punctuation cleanup, Bangla/English aliases and transliteration (future), structured category IDs and supported attributes.
3. **Scores:** category `20%`, title `20%`, description `20%`, attributes `15%`, location `15%`, time `10%` (experimental starting values).
4. **Missing fields:** exclude unavailable feature and renormalize present weights; preserve `missingFeatures` in explanation. Do not reward missing information as a perfect match.
5. **Conflicts:** mark hard contradictions (e.g. demonstrably different serial/unique private traits) as negative evidence subject to privacy restrictions.
6. **Time:** distinguish event window from report creation; timezone and uncertainty aware.
7. **Location:** straight-line separation is an indicator, not a strict condition; allow transit displacement and broad/nationwide fallback.
8. **Score:** `100 * sum(weight_i * similarity_i) / sum(active_weight_i)`; clamp `0..100`, store algorithm version.
9. **Action:** suggest higher ranked candidates; notification thresholds only after measured false-alert rate is acceptable.
10. **Evaluation:** build labeled true/false report pairs; track precision, recall, Precision@K, false alerts and median matching latency.

Never use a high score by itself to approve a claim or release an item.

## 11. Map/geocoding contract

**Frontend:** Leaflet renders tiles, chosen coordinates, and public approximate markers. Browser geolocation requires consent. A place-search/geocoder is a separate service; public OpenStreetMap services are not automatically unlimited/free for production.
**Backend:** validate lat/lng; store user-provided accuracy; calculate distance using MySQL spatial functions or an indexed bounding box plus great-circle distance; omit/private-round coordinates in public DTO.
**Endpoint rules:** require both `lat` and `lng`, validate `radiusMeters` positive and capped, bound page size, document straight-line vs route distance, avoid exposing home locations.
**JourneyMatch (future):** route planner → polyline buffer → nearby reports with plausible event times → match ranking. Alternative paths and uncertain itinerary supported.

## 12. Notifications and event processing

| Trigger                      | Recipient                       | Privacy rule                                     |
| ---------------------------- | ------------------------------- | ------------------------------------------------ |
| Strong candidate created     | Report owners                   | No hidden evidence in notification payload       |
| Claim submitted              | Finder/reporter of FOUND report | Show only authorized summary                     |
| Claim approved/rejected      | Claimant                        | No unrelated users' information                  |
| Recovery arrangement updated | Participating parties           | Private location only to authorized participants |
| Report removed by moderator  | Report owner                    | Safe rationale, appeal guidance                  |

Event handlers must be retry-safe: use unique event IDs or a notification-deduplication key. When matching grows, consider an outbox pattern to avoid losing events between database commit and worker execution.

## 13. Configuration, environment, deployment

| Variable                 | Purpose                               | Handling                                 |
| ------------------------ | ------------------------------------- | ---------------------------------------- |
| `APP_ENV`              | Environment selection                 | dev/staging/prod                         |
| `PORT`                 | HTTP listener                         | hosting-controlled where relevant        |
| `DB_URL`               | JDBC database connection              | secret/config                            |
| `DB_USERNAME`          | Database user                         | secret                                   |
| `DB_PASSWORD`          | Database password                     | secret, never committed                  |
| `JWT_SECRET`           | Token signing material                | high-entropy secret, rotated responsibly |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins              | explicit production hosts                |
| `FILE_STORAGE_*`       | Upload provider settings              | private secret/config                    |
| `GEOCODER_*`           | Geocoding provider credentials/limits | provider-dependent                       |
| `MATCH_*`              | Weights, thresholds, worker settings  | versioned config                         |

**Secret hygiene:** Older chats may have contained real-looking credentials; rotate exposed tokens/passwords, invalidate old values, remove secrets from Git history as appropriate, and use environment-based secret management. Never document secret values.

**Environments:** local → staging → production, each with distinct database/storage/keys.
**Release:** migration dry run, regression suite, frontend build, smoke tests, backup, deploy, verify, rollback instructions.
**Observability:** structured logs with trace ID, request metrics, worker success/failure, slow SQL, alerting. Avoid logging private claim evidence or JWTs.
**Backups:** define RPO/RTO, automated backups, periodic restore tests.

## 14. Testing and acceptance matrix

| Scenario                          | Expected behavior                       | Test type               |
| --------------------------------- | --------------------------------------- | ----------------------- |
| Signup duplicate email            | No duplicate user; consistent error     | Integration             |
| Wrong JWT                         | `401`, no data leak                   | Security                |
| Update someone else's item        | `403` or safe `404`, no update      | Security                |
| FOUND query within 3 km           | Only eligible nearby reports            | Integration             |
| Missing/uncertain location        | Item still searchable by text           | Unit/integration        |
| Same candidate event twice        | One stored pair, no duplicate push      | Integration             |
| Different owners' similar wallets | Candidate only, no auto-approval        | Domain test             |
| Claim without authorization       | Rejected, private fields hidden         | Security                |
| Concurrent claim approvals        | State invariant preserved               | Integration/concurrency |
| Public map query                  | No exact private handover address       | Security                |
| Recover item successfully         | Audit, status, notifications consistent | E2E                     |
| Upload sensitive document         | Stored privately/redacted as designed   | Security/E2E            |
| Bangla/English place variants     | Supported normalization evaluated       | Search relevance        |

**Minimum acceptance for nationwide search release:** correct location coordinates, pagination, authorization, no sensitive public leaks, measured query performance, E2E report→search→claim happy path, failing paths tested.

## 15. Development phases and definitions of done

| Phase | Deliverable                       | Definition of done                                                           |
| ----- | --------------------------------- | ---------------------------------------------------------------------------- |
| 0     | Source audit and contract capture | Actual controller paths, DTOs, entities, configs, schema, tests recorded     |
| 1     | Location reporting                | Pin/approximate area persisted, public-safe DTO, tests                       |
| 2     | Unified search                    | Text/category/date/radius + pagination + explainable ranking                 |
| 3     | SmartMatch V1                     | Durable pair and evidence, retry-safe worker, notification policy, benchmark |
| 4     | Verification/recovery             | Private claim evidence, handover confirmation, auditing, abuse controls      |
| 5     | Pilot launch                      | Pilot users, operational metrics, monitoring, restore test, support process  |
| 6     | Advanced matching                 | Evaluation-based semantic/image/route features, opt-in where necessary       |

### Phase 0 source-audit checklist (must do before calling this document 'as-built')

- [ ] Record commit SHA and branch audited (`develop` preferred if it is active).
- [ ] Enumerate controllers with HTTP mappings and authorization.
- [ ] Record exact JSON request and response DTO fields.
- [ ] Map every endpoint to service and repository calls.
- [ ] Capture current JPA entity relationships and MySQL schema.
- [ ] Inventory JWT filters and `SecurityConfig` route rules.
- [ ] Inspect notification and file upload integrations.
- [ ] Enumerate frontend pages/components and exact API calls.
- [ ] Generate or compare OpenAPI contract.
- [ ] Reconcile this document's `VERIFY` placeholders with real filenames/behavior.
- [ ] Record test results and known discrepancies.

## 16. Documentation maintenance standard

Every API or feature PR must update **together**:

1. The API contract: method/path/auth, request/response and error shapes.
2. The end-to-end flow: UI trigger through service/repository to side effects.
3. The affected file map and ownership.
4. Schema changes and migration notes.
5. Security implications and tests.
6. Release notes for any breaking change.

**Source of truth:** Actual source code and generated OpenAPI for implemented behavior; this blueprint for proposed behavior. Never mark a proposed endpoint as implemented without a successful source/test check.


# Development Timeline — 14 Days

**Project:** Lost & Found Bangladesh
**Duration:** 14 Days
**Start Date:** November 24, 2026
**Target Completion:** December 7, 2026
**Development Status:** Planned
**Team:** Solo Developer
**Estimated Effort:** 4–6 Hours/Day (56–84 Hours)

## 1. Objective

Upgrade the existing Spring Boot Lost & Found application into a functional nationwide MVP with geographical search, automated matching, improved ownership verification, and a responsive frontend.

The existing authentication, reporting, claims, and notification functionality will be reused.

**Target:** Deliver a working, tested public beta within 14 days.

## 2. Development Schedule

| Day | Date   | Tasks                                                              | Deliverable               |
| --- | ------ | ------------------------------------------------------------------ | ------------------------- |
| 1   | Nov 24 | Audit existing code, fix critical bugs, verify APIs                | Stable backend            |
| 2   | Nov 25 | Improve JWT security, DTO validation, roles and permissions        | Secure authentication     |
| 3   | Nov 26 | Improve item entities, categories, attributes, database migrations | Updated database          |
| 4   | Nov 27 | Implement frontend design system, homepage, navigation             | Responsive UI foundation  |
| 5   | Nov 28 | Build reporting, item details, authentication, dashboard           | Core frontend             |
| 6   | Nov 29 | Integrate Leaflet, map selection, coordinates                      | Map-enabled reporting     |
| 7   | Nov 30 | Implement geographical distance and nearby search                  | Nearby discovery          |
| 8   | Dec 1  | Develop unified search, filters, pagination                        | Unified search API and UI |
| 9   | Dec 2  | Implement SmartMatch candidate retrieval and similarity scoring    | SmartMatch V1             |
| 10  | Dec 3  | Integrate automatic matching, match storage, notifications         | Automated matching        |
| 11  | Dec 4  | Improve claim verification and secure ownership evidence           | Verified claims           |
| 12  | Dec 5  | Implement basic recovery confirmation and moderation               | Recovery workflow         |
| 13  | Dec 6  | Run unit, integration, security, and frontend tests                | Release candidate         |
| 14  | Dec 7  | Fix critical issues, deploy, verify, update documentation          | Public beta               |

## 3. Daily Milestones

### Days 1–3: Backend Foundation

**Goals:**

- Stabilize existing functionality.
- Fix known claim rejection issue if still present.
- Improve validation and authorization.
- Extend database schema for nationwide features.
- Preserve existing API compatibility.

**Completion Criteria:**

- Backend builds successfully.
- Existing critical APIs function correctly.
- Database migrations apply successfully.
- Authentication and authorization tests pass.

### Days 4–5: Frontend Development

**Goals:**

- Establish reusable components.
- Implement responsive layouts.
- Integrate existing authentication APIs.
- Complete lost/found reporting interfaces.
- Integrate claims and notifications.

**Completion Criteria:**

- Frontend builds successfully.
- Existing core user journeys work.
- Mobile and desktop layouts are usable.

### Days 6–8: Location & Unified Search

**Goals:**

- Implement map-based location selection.
- Store geographic coordinates.
- Support approximate locations.
- Implement radius-based search.
- Add text, category, date, and location filters.

**Completion Criteria:**

- Users can select report locations on a map.
- Nearby search returns geographically relevant reports.
- Unified search supports combined filters.
- Public results protect sensitive coordinates.

### Days 9–10: SmartMatch V1

**Goals:**

- Retrieve relevant LOST/FOUND candidates.
- Calculate rule-based similarity.
- Combine title, category, attributes, location, and time.
- Store matching results.
- Trigger notifications after successful matching.

**Completion Criteria:**

- New reports trigger matching.
- Matching scores are reproducible.
- Duplicate matches are prevented.
- Users can view potential matches.

### Days 11–12: Ownership & Recovery

**Goals:**

- Strengthen claim verification.
- Protect private ownership evidence.
- Implement safe handover status tracking.
- Add basic moderation capabilities.

**Completion Criteria:**

- Unauthorized claims cannot be approved.
- Private evidence remains protected.
- Valid recovery state transitions work.
- Completed recoveries are recorded.

### Days 13–14: Testing & Deployment

**Goals:**

- Run automated tests.
- Verify complete user journeys.
- Fix critical defects.
- Review security.
- Deploy frontend and backend.
- Update technical documentation.

**Completion Criteria:**

- Critical tests pass.
- Production build succeeds.
- Core APIs work in the deployed environment.
- Critical security vulnerabilities are addressed.
- End-to-end recovery workflow is verified.

## 4. Feature Priorities

| Priority        | Features                                                                                       |
| --------------- | ---------------------------------------------------------------------------------------------- |
| P0 — Required  | Authentication, reporting, map selection, nearby search, unified search, SmartMatch V1, claims |
| P1 — Important | Match notifications, recovery confirmation, basic moderation                                   |
| P2 — Optional  | Advanced analytics, multilingual improvements                                                  |
| Future          | Image similarity, semantic AI, OCR, JourneyMatch, mobile apps                                  |

If development falls behind schedule, complete P0 features before starting P1 or P2 work.

Do not sacrifice security or data integrity to meet the deadline.

## 5. Technology Stack

| Component      | Technology                          |
| -------------- | ----------------------------------- |
| Backend        | Java 21, Spring Boot                |
| Frontend       | React                               |
| Database       | MySQL                               |
| Authentication | Spring Security, JWT                |
| Maps           | Leaflet, OpenStreetMap              |
| Matching       | Rule-based weighted algorithm       |
| Testing        | JUnit, Spring Boot Test             |
| Deployment     | Docker, Render/Vercel or equivalent |

## 6. Development Workflow

Every feature should follow:

```text
Requirement
    ↓
Database / API Design
    ↓
Backend Implementation
    ↓
Frontend Integration
    ↓
Automated Testing
    ↓
Manual Verification
    ↓
Documentation Update
```

Commit completed changes regularly.

Do not introduce unrelated architectural changes during this development cycle.

## 7. Final Deliverables

By December 7, 2026, the target deliverables are:

- Functional Spring Boot backend
- Responsive React frontend
- Nationwide map-based reporting
- Nearby-item search
- Unified search and filtering
- SmartMatch V1
- Match notifications
- Ownership claims and verification
- Basic recovery management
- Automated tests
- Updated documentation
- Public beta deployment

## 8. Risks and Limitations

The 14-day schedule is an aggressive MVP target.

Potential risks include:

- Existing backend defects
- Database migration conflicts
- Map/geocoding integration issues
- Insufficient test coverage
- Matching inaccuracies
- Deployment configuration problems

If critical features cannot be completed safely, defer them rather than releasing incomplete security-sensitive functionality.

Advanced AI, production-scale optimization, and institutional integrations are outside this timeline.

## 9. Definition of Done

The 14-day MVP is considered complete when:

1. Core reporting and discovery workflows function.
2. Map-based and unified search work.
3. SmartMatch V1 generates explainable potential matches.
4. Claims enforce appropriate authorization.
5. Sensitive user information is protected.
6. Critical automated tests pass.
7. The deployed application supports the main recovery journey.
8. Documentation reflects the implemented functionality.

**Final Goal:** Deliver a secure, functional nationwide Lost & Found MVP with intelligent matching and geographical discovery.

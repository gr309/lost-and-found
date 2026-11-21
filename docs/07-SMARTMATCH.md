
# SmartMatch — Intelligent Matching Engine

**Project:** Lost & Found Bangladesh
**Version:** 1.0
**Status:** Planned
**Backend:** Spring Boot
**Initial Approach:** Rule-Based Hybrid Matching

## 1. Overview

SmartMatch automatically identifies potential matches between LOST and FOUND reports using item characteristics, text similarity, geographical proximity, and time compatibility.

**Objectives:**

- Automatically discover potential matches.
- Reduce manual searching.
- Rank relevant reports.
- Explain matching decisions.
- Notify relevant users.

**Important:** A matching score indicates similarity, not verified ownership.

## 2. Matching Workflow

```text
New / Updated Item
        |
        v
Database Transaction Committed
        |
        v
Matching Trigger
        |
        v
Candidate Retrieval
        |
        v
Data Normalization
        |
        v
Similarity Calculation
        |
        v
Weighted Match Score
        |
        v
Save / Update Matches
        |
        v
NotificationService
```

Matching should execute asynchronously to avoid delaying report creation.

## 3. Matching Rules

1. LOST reports are compared only against FOUND reports.
2. Inactive or resolved reports are excluded unless explicitly needed.
3. Duplicate match pairs are prohibited.
4. Missing attributes must not automatically produce zero similarity.
5. Geographic distance is a ranking factor, not an absolute rejection rule.
6. Matching must support delayed reporting.
7. Sensitive identifying information must not appear in public match explanations.

## 4. Candidate Retrieval

The system retrieves a limited number of potentially relevant reports before scoring.

### Retrieval Filters

- Opposite report type
- Compatible category
- Active report status
- Relevant keywords
- Geographic proximity
- Plausible event dates

If nearby candidates are insufficient, the system may perform a broader nationwide search.

### Example

```text
New Report: LOST Wallet
Location: Rajshahi
Date: 2026-11-20

Step 1: Search nearby FOUND wallets.
Step 2: Expand to related categories.
Step 3: Search a broader geographical area.
Step 4: Rank eligible candidates.
```

Candidate retrieval must not rely solely on exact keywords.

## 5. Similarity Factors

| Factor          | Initial Weight |
| --------------- | -------------: |
| Category        |            20% |
| Title           |            20% |
| Description     |            20% |
| Attributes      |            15% |
| Location        |            15% |
| Time            |            10% |
| **Total** | **100%** |

These weights are provisional and must be evaluated against labeled matching examples.

## 6. Matching Algorithm

Each similarity component returns a normalized score between `0.0` and `1.0`.

### Formula

```text
MatchScore = 100 × (
    0.20 × CategorySimilarity
  + 0.20 × TitleSimilarity
  + 0.20 × DescriptionSimilarity
  + 0.15 × AttributeSimilarity
  + 0.15 × LocationSimilarity
  + 0.10 × TimeSimilarity
)
```

### Example

| Factor      | Similarity |
| ----------- | ---------: |
| Category    |       1.00 |
| Title       |       0.90 |
| Description |       0.85 |
| Attributes  |       0.90 |
| Location    |       0.80 |
| Time        |       0.95 |

**Final Score: 89.5/100**

The score represents algorithmic similarity, not the probability that the reports describe the same physical item.

## 7. Text Similarity

### Version 1

Use lightweight techniques:

- Lowercase normalization
- Whitespace normalization
- Punctuation removal
- Token-based similarity
- Category aliases
- Common synonym mappings

Example:

```text
"Black Leather Wallet"
"Dark Wallet"

Normalized concepts:
wallet, black/dark, leather
```

### Future Version

Introduce multilingual semantic embeddings to better understand Bangla, English, and transliterated descriptions.

## 8. Location Similarity

Use latitude and longitude to calculate geographical distance.

### Example

```text
Lost Location: Shaheb Bazar
Found Location: Nearby Market

Distance: 650 meters
```

A possible distance-decay function:

```text
LocationSimilarity = exp(-distance / scale)
```

Where:

- `distance` is measured in meters.
- `scale` is a configurable positive distance parameter.

The system should consider location uncertainty and avoid rejecting geographically distant reports automatically.

## 9. Time Similarity

Compare when an item was lost with when it was found.

Consider:

- Event date
- Approximate time window
- Reporting delay
- Unknown or uncertain times
- Chronological plausibility

**Example:**

```text
Lost: 20 November, 3:00 PM
Found: 20 November, 4:00 PM
```

This is temporally plausible.

The system must distinguish event time from report creation time.

## 10. Missing Information

Not all users will provide complete details.

Example:

```json
{
  "title": "Found Wallet",
  "category": "WALLET",
  "color": null,
  "eventDate": "2026-11-20"
}
```

Missing fields must not be treated as confirmed mismatches.

The scoring system should normalize available feature weights and retain a separate data-completeness indicator.

Low-information reports may require additional verification before notifying users.

## 11. Match Classification

| Score    | Classification | Suggested Action            |
| -------- | -------------- | --------------------------- |
| 85–100  | Very strong    | Notify eligible users       |
| 70–84   | Strong         | Recommend                   |
| 50–69   | Possible       | Display as suggestion       |
| Below 50 | Weak           | Do not notify automatically |

Thresholds are initial assumptions and should be calibrated using evaluation results.

## 12. Backend Components

### Proposed File Structure

```text
matching/
├── MatchingService.java
├── MatchingTrigger.java
├── CandidateRetrievalService.java
├── SimilarityScoringService.java
├── CategorySimilarity.java
├── TextSimilarity.java
├── AttributeSimilarity.java
├── LocationSimilarity.java
├── TimeSimilarity.java
└── MatchScoreResult.java

entity/
└── ItemMatch.java

repository/
└── ItemMatchRepository.java

dto/
└── MatchResponse.java
```

### File Responsibilities

| File                          | Responsibility                      |
| ----------------------------- | ----------------------------------- |
| `MatchingTrigger`           | Start matching after report changes |
| `MatchingService`           | Coordinate the matching workflow    |
| `CandidateRetrievalService` | Retrieve eligible candidates        |
| `SimilarityScoringService`  | Combine component scores            |
| `CategorySimilarity`        | Compare categories                  |
| `TextSimilarity`            | Compare titles/descriptions         |
| `AttributeSimilarity`       | Compare structured attributes       |
| `LocationSimilarity`        | Evaluate geographical proximity     |
| `TimeSimilarity`            | Evaluate temporal compatibility     |
| `MatchScoreResult`          | Hold score and explanation          |
| `ItemMatchRepository`       | Persist and retrieve matches        |

These are proposed components and should be adapted to the existing codebase.

## 13. Database Integration

### `item_matches`

```text
id
lost_item_id
found_item_id
match_score
match_status
algorithm_version
created_at
updated_at
```

**Constraint:**

```sql
UNIQUE (lost_item_id, found_item_id)
```

### `match_evidence`

```text
id
match_id
feature_name
similarity_score
weight
contribution
```

See `04-DATABASE.md` for the complete database design.

## 14. Automatic Notification

### Trigger

A new eligible match exceeds the configured notification threshold.

```text
MatchingService
      |
      v
Save Match
      |
      v
Check Notification Rules
      |
      v
NotificationService
      |
      v
Notify Relevant User
```

### Example Notification

```json
{
  "type": "MATCH_FOUND",
  "title": "Potential Match Found",
  "message": "A similar item has been reported near your selected location.",
  "data": {
    "matchId": 25,
    "itemId": 12
  }
}
```

Avoid duplicate notifications for unchanged matches.

## 15. Internal Execution Flow

```text
ItemService
    |
    v
Report Saved
    |
    v
Transaction Committed
    |
    v
MatchingTrigger
    |
    v
MatchingService
    |
    +-- ItemRepository
    |
    +-- CandidateRetrievalService
    |
    +-- SimilarityScoringService
    |       |
    |       +-- CategorySimilarity
    |       +-- TextSimilarity
    |       +-- AttributeSimilarity
    |       +-- LocationSimilarity
    |       +-- TimeSimilarity
    |
    +-- ItemMatchRepository
    |
    +-- NotificationService
```

## 16. Failure Handling

The matching engine must handle:

- No matching candidates
- Missing attributes
- Invalid or approximate coordinates
- Duplicate candidate pairs
- Database errors
- Failed background jobs
- Notification failures

Failed tasks should be logged and retried safely when appropriate.

Report creation should not fail merely because background matching fails.

## 17. Performance Considerations

- Retrieve candidates before running expensive similarity calculations.
- Use indexed filters.
- Avoid comparing every report against every other report.
- Use pagination or bounded candidate batches.
- Cache reusable normalized data where appropriate.
- Process matching asynchronously.
- Record matching execution time.

For larger datasets, consider dedicated search infrastructure.

## 18. Testing Strategy

### Unit Tests

- Exact category match
- Similar item titles
- Different item categories
- Nearby locations
- Distant locations
- Overlapping time windows
- Missing attributes
- Score normalization

### Integration Tests

- New report triggers matching.
- LOST matches only FOUND.
- Duplicate matches are prevented.
- Matches persist correctly.
- Notifications are generated only when eligible.
- Failed matching does not corrupt reports.

### Evaluation Metrics

- Precision
- Recall
- F1-score
- Precision@K
- Recall@K
- False notification rate

Create a labeled dataset containing both true matches and difficult non-matches.

## 19. Future Improvements

Potential enhancements:

1. Semantic text embeddings.
2. Bangla-English multilingual matching.
3. Image similarity.
4. OCR-assisted item identification.
5. Journey-based location matching.
6. Feedback-based ranking.
7. Calibrated match confidence.

Each enhancement should be evaluated against the previous baseline before adoption.

## 20. Related Documentation

- `04-DATABASE.md` — Matching tables
- `05-API.md` — Matching endpoints
- `06-CODEBASE.md` — File responsibilities
- `08-SECURITY.md` — Authorization and privacy
- `09-TESTING.md` — Testing requirements

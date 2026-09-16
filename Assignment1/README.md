# Assignment 1

## Concurrency Testing

The application includes regression tests for concurrency.

### HTTP concurrency
250 simultaneous blocking requests were sent through the transcription controller.

Observed result:
- 250 successful requests
- 250 maximum concurrent requests
- approximately 1.4 seconds total runtime

### Shared-state race condition
1000 concurrent token usage updates were performed against GlobalStatsService.

Expected:
- 10,000 input tokens
- 5,000 output tokens

Actual:
- Exact expected values were maintained

AtomicLong is used to prevent lost updates during concurrent access.

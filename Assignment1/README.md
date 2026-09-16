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

### GlobalExceptionHandler / 500 Response Test

Regression test for unexpected server errors.

- Mocks `GlobalStatsService`
- Forces the service to throw a runtime exception
- Sends a GET request to `/api/v1/global/stats`
- Verifies HTTP `500 Internal Server Error`
- Verifies the response matches the OpenAPI `ErrorResponse` structure
- Verifies `status`, `error`, `message`, `path`, and `timestamp`

This confirms unexpected backend failures are converted into a consistent JSON error response rather than exposing raw exceptions to API clients.
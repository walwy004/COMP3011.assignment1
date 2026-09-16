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

### TranscriptionControllerTest

Regression test for `POST /api/v1/transcriptions`.

- Uses a mocked `TranscriptionService` rather than the live OpenAI Cloud service
- Uploads a simulated WebM audio file using a multipart HTTP request
- Verifies the controller accepts the `audio` multipart field
- Verifies HTTP `200 OK`
- Verifies the returned JSON contains the expected transcription text

#### Unexpected transcription failure

- Forces the mocked `TranscriptionService` to throw an exception
- Verifies HTTP `500 Internal Server Error`
- Verifies the response uses the standard OpenAPI `ErrorResponse` format
- Verifies the status, error, message, request path and timestamp

The STT service is mocked so the test is deterministic, does not require an API key, does not consume Cloud API resources, and verifies the REST controller independently of the external OpenAI service.


# Assignment 1
## Speech-to-Text Web Application

This project is a Spring Boot speech-to-text web application developed for COMP3011 Assignment 1.

The application allows a browser client to record audio from the user's microphone, upload the recording to a Java backend, transcribe the audio using the OpenAI Speech-to-Text API, display the resulting transcription, expose server administration and statistics endpoints, and support graceful server shutdown.

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

Regression tests for `POST /api/v1/transcriptions`.

#### Successful transcription request

- Uses a mocked `TranscriptionService` instead of the live OpenAI service
- Uploads a simulated WebM audio file using a multipart request
- Verifies HTTP `200 OK`
- Verifies the returned JSON contains the expected transcription text

These tests confirm the transcription controller behaves correctly without requiring
a live API key or consuming external Cloud resources.

#### Unexpected transcription failure

- Forces the mocked `TranscriptionService` to throw an exception
- Verifies HTTP `500 Internal Server Error`
- Verifies the response uses the standard OpenAPI `ErrorResponse` format
- Verifies the status, error, message, request path and timestamp

### AdminControllerTest for uptime

Regression tests for `GET /api/v1/admin/uptime`.

#### Successful uptime request

- Mocks `UptimeService` with known server start and current timestamps
- Sends a GET request through `MockMvc`
- Verifies HTTP `200 OK`
- Verifies `utcServerStart`
- Verifies `utcNow`
- Verifies `serverUptimeSeconds`

This confirms that the uptime controller continues to produce the response
format defined by the OpenAPI specification.

#### Unexpected uptime failure

- Forces the mocked `UptimeService` to throw an exception
- Verifies HTTP `500 Internal Server Error`
- Verifies the standard OpenAPI `ErrorResponse`
- Verifies the status, error, message, request path and timestamp

This ensures unexpected failures are handled consistently without exposing
internal exception details to API clients.

### ShutdownControllerTest

Regression tests for `POST /api/v1/admin/shutdown`.

The real `ShutdownService` is mocked so the tests do not actually terminate
the Spring Boot application.

#### Accepted shutdown request

- Mocks `ShutdownService.shutdown()` to return `true`
- Verifies HTTP `202 Accepted`
- Verifies the response contains `"Graceful shutdown requested."`

#### Shutdown already in progress

- Mocks `ShutdownService.shutdown()` to return `false`
- Verifies HTTP `409 Conflict`
- Verifies the response matches the OpenAPI `ErrorResponse` structure
- Verifies the status, error, message, request path and timestamp

#### Unexpected shutdown failure

- Forces the mocked service to throw an exception
- Verifies HTTP `500 Internal Server Error`
- Verifies the global exception handler returns the standard OpenAPI error format

These tests ensure that changes to the shutdown controller do not break its
documented `202`, `409` or `500` REST API behaviour.

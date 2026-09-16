package comp3011.assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.dto.TranscriptionResponse;
import comp3011.assignment1.service.TranscriptionService;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.threads.virtual.enabled=true",
                "spring.main.keep-alive=true"
        }
)
class TranscriptionConcurrencyTest {

    private static final int REQUEST_COUNT = 250;

    @Value("${local.server.port}")
    private int port;

    @MockitoBean
    private TranscriptionService transcriptionService;

    @Test
    void handlesMoreThan200ConcurrentBlockingRequests() throws Exception {

        AtomicInteger activeRequests = new AtomicInteger(0);
        AtomicInteger maxConcurrentRequests = new AtomicInteger(0);

        /*
         * Replace the real OpenAI call with a deliberately blocking stub.
         *
         * Every request sleeps for one second so that many HTTP requests
         * remain active at the same time.
         */
        when(transcriptionService.transcribe(any(MultipartFile.class)))
                .thenAnswer(invocation -> {

                    int active = activeRequests.incrementAndGet();

                    maxConcurrentRequests.updateAndGet(
                            currentMax -> Math.max(currentMax, active)
                    );

                    try {
                        Thread.sleep(1000);
                        return "Test transcription";
                    } finally {
                        activeRequests.decrementAndGet();
                    }
                });

        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        CountDownLatch startLatch = new CountDownLatch(1);

        List<Future<Integer>> futures = new ArrayList<>();

        long startTime = System.nanoTime();

        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < REQUEST_COUNT; i++) {

                futures.add(executor.submit(() -> {

                    // Make all 250 client requests begin together
                    startLatch.await();

                    MultipartBodyBuilder body =
                            new MultipartBodyBuilder();

                    ByteArrayResource audio =
                            new ByteArrayResource(
                                    "fake audio".getBytes()
                            ) {
                                @Override
                                public String getFilename() {
                                    return "recording.webm";
                                }
                            };

                    body.part(
                            "audio",
                            audio,
                            MediaType.parseMediaType("audio/webm")
                    );

                    TranscriptionResponse response =
                            client.post()
                                    .uri("/api/v1/transcriptions")
                                    .contentType(
                                            MediaType.MULTIPART_FORM_DATA
                                    )
                                    .body(body.build())
                                    .retrieve()
                                    .body(
                                            TranscriptionResponse.class
                                    );

                    return response != null
                            && "Test transcription".equals(
                                    response.text()
                            )
                            ? 1
                            : 0;
                }));
            }

            // Release all requests at approximately the same time
            startLatch.countDown();

            int successfulRequests = 0;

            for (Future<Integer> future : futures) {
                successfulRequests += future.get();
            }

            long elapsedMs =
                    (System.nanoTime() - startTime)
                    / 1_000_000;

            System.out.println(
                    "Successful requests: "
                    + successfulRequests
            );

            System.out.println(
                    "Maximum concurrent requests: "
                    + maxConcurrentRequests.get()
            );

            System.out.println(
                    "Total duration: "
                    + elapsedMs
                    + " ms"
            );

            assertEquals(
                    REQUEST_COUNT,
                    successfulRequests
            );

            assertTrue(
                    maxConcurrentRequests.get() > 200,
                    "Server should process more than 200 "
                    + "blocking requests concurrently"
            );

            assertTrue(
                    elapsedMs < 10_000,
                    "250 requests took too long: "
                    + elapsedMs
                    + " ms"
            );
        }
    }
}
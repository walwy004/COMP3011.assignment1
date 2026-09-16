package comp3011.assignment1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import comp3011.assignment1.dto.GlobalStatsResponse;
import comp3011.assignment1.service.GlobalStatsService;

class GlobalStatsServiceConcurrencyTest {

    @Test
    void handlesConcurrentTokenUpdatesWithoutLosingData() throws Exception {

        GlobalStatsService service = new GlobalStatsService();

        int requestCount = 1000;

        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {

            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < requestCount; i++) {

                futures.add(
                    executor.submit(() -> {
                        service.addTokenUsage(10, 5);
                    })
                );
            }

            // Wait for every update to finish
            for (Future<?> future : futures) {
                future.get();
            }
        }

        GlobalStatsResponse stats =
                service.getGlobalStats();

        assertEquals(
                10000,
                stats.inputTokens()
        );

        assertEquals(
                5000,
                stats.outputTokens()
        );
    }
}

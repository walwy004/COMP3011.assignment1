package comp3011.assignment1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment1.controller.AdminController;
import comp3011.assignment1.dto.UptimeResponse;
import comp3011.assignment1.exception.GlobalExceptionHandler;
import comp3011.assignment1.service.ShutdownService;
import comp3011.assignment1.service.UptimeService;

@WebMvcTest(AdminController.class)
@Import(GlobalExceptionHandler.class)
class AdminUptimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UptimeService uptimeService;

    @MockitoBean
    private ShutdownService shutdownService;

    @Test
    void returnsServerUptime() throws Exception {

        Instant serverStart = Instant.parse("2026-07-14T01:15:30Z");

        Instant now = Instant.parse("2026-07-14T03:45:30.500Z");

        when(uptimeService.getUptime())
                .thenReturn(
                        new UptimeResponse(
                                serverStart,
                                now,
                                9000.5
                        )
                );

        mockMvc.perform(
                get("/api/v1/admin/uptime")
        )
        .andExpect(status().isOk())
        .andExpect(
                jsonPath("$.utcServerStart")
                        .value("2026-07-14T01:15:30Z")
        )
        .andExpect(
                jsonPath("$.utcNow")
                        .value("2026-07-14T03:45:30.500Z")
        )
        .andExpect(
                jsonPath("$.serverUptimeSeconds")
                        .value(9000.5)
        );
    }

    @Test
    void returns500WhenUptimeServiceFails() throws Exception {

        when(uptimeService.getUptime())
                .thenThrow(
                        new RuntimeException("Test failure")
                );

        mockMvc.perform(
                get("/api/v1/admin/uptime")
        )
        .andExpect(
                status().isInternalServerError()
        )
        .andExpect(
                jsonPath("$.status").value(500)
        )
        .andExpect(
                jsonPath("$.error")
                        .value("Internal Server Error")
        )
        .andExpect(
                jsonPath("$.message")
                        .value(
                                "An unexpected server error occurred."
                        )
        )
        .andExpect(
                jsonPath("$.path")
                        .value("/api/v1/admin/uptime")
        )
        .andExpect(
                jsonPath("$.timestamp").exists()
        );
    }
}

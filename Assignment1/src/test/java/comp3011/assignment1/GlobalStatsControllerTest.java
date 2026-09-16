package comp3011.assignment1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment1.controller.GlobalStatsController;
import comp3011.assignment1.dto.GlobalStatsResponse;
import comp3011.assignment1.service.GlobalStatsService;

@WebMvcTest(GlobalStatsController.class)
class GlobalStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GlobalStatsService globalStatsService;

    @Test
    void returnsGlobalStats() throws Exception {

        when(globalStatsService.getGlobalStats())
                .thenReturn(
                        new GlobalStatsResponse(1000, 500)
                );

        mockMvc.perform(
                get("/api/v1/global/stats")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.inputTokens").value(1000))
        .andExpect(jsonPath("$.outputTokens").value(500));
    }

    @Test
    void returns500WhenServiceThrowsException() throws Exception {

        when(globalStatsService.getGlobalStats())
                .thenThrow(
                        new RuntimeException("Test failure")
                );

        mockMvc.perform(
                get("/api/v1/global/stats")
        )
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error")
                .value("Internal Server Error"))
        .andExpect(jsonPath("$.message")
                .value("An unexpected server error occurred."))
        .andExpect(jsonPath("$.path")
                .value("/api/v1/global/stats"))
        .andExpect(jsonPath("$.timestamp").exists());
    }
}

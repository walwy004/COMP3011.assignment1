package comp3011.assignment1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import comp3011.assignment1.controller.AdminController;
import comp3011.assignment1.exception.GlobalExceptionHandler;
import comp3011.assignment1.service.ShutdownService;
import comp3011.assignment1.service.UptimeService;

@WebMvcTest(AdminController.class)
@Import(GlobalExceptionHandler.class)
class ShutdownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShutdownService shutdownService;
    
    @MockitoBean
    private UptimeService uptimeService;

    @Test
    void acceptsFirstShutdownRequest() throws Exception {

        when(shutdownService.shutdownServer())
                .thenReturn(true);

        mockMvc.perform(
                post("/api/v1/admin/shutdown")
        )
        .andExpect(status().isAccepted())
        .andExpect(
                jsonPath("$.message")
                        .value("Graceful shutdown requested.")
        );
    }

    @Test
    void returns409WhenShutdownAlreadyInProgress() throws Exception {

        when(shutdownService.shutdownServer())
                .thenReturn(false);

        mockMvc.perform(
                post("/api/v1/admin/shutdown")
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(
                jsonPath("$.error")
                        .value("Conflict")
        )
        .andExpect(
                jsonPath("$.message")
                        .value(
                            "Graceful shutdown is already in progress."
                        )
        )
        .andExpect(
                jsonPath("$.path")
                        .value("/api/v1/admin/shutdown")
        )
        .andExpect(
                jsonPath("$.timestamp").exists()
        );
    }

    @Test
    void returns500WhenShutdownServiceFails() throws Exception {

        when(shutdownService.shutdownServer())
                .thenThrow(
                        new RuntimeException("Test failure")
                );

        mockMvc.perform(
                post("/api/v1/admin/shutdown")
        )
        .andExpect(
                status().isInternalServerError()
        )
        .andExpect(jsonPath("$.status").value(500))
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
                        .value("/api/v1/admin/shutdown")
        )
        .andExpect(
                jsonPath("$.timestamp").exists()
        );
    }
}

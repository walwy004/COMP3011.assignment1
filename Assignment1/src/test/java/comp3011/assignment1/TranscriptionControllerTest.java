package comp3011.assignment1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.controller.TranscriptionController;
import comp3011.assignment1.service.TranscriptionService;

@WebMvcTest(TranscriptionController.class)
class TranscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TranscriptionService transcriptionService;

    @Test
    void returnsTranscriptionForUploadedAudio() throws Exception {

        when(transcriptionService.transcribe(
                any(MultipartFile.class)))
                .thenReturn("Hello, this is a test transcription.");

        MockMultipartFile audio =
                new MockMultipartFile(
                        "audio",
                        "recording.webm",
                        "audio/webm",
                        "fake audio data".getBytes()
                );

        mockMvc.perform(
                multipart("/api/v1/transcriptions")
                        .file(audio)
        )
        .andExpect(status().isOk())
        .andExpect(
                jsonPath("$.text")
                        .value(
                            "Hello, this is a test transcription."
                        )
        );
    }
}

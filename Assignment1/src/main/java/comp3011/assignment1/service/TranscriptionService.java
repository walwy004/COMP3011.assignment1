package comp3011.assignment1.service;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.dto.OpenAITranscriptionResponse;

@Service
public class TranscriptionService {
	
	private final GlobalStatsService globalStatsService;
	private final RestClient restClient;
	private final String apiKey;
	
	public TranscriptionService(GlobalStatsService globalStatsService) {
		
		this.globalStatsService = globalStatsService;
		this.apiKey = System.getenv("OPENAI_API_KEY");
		this.restClient = RestClient.builder()
				.baseUrl("https://api.openai.com")
				.build();
	}
	
	public String transcribe(MultipartFile audio) throws IOException {
		
		MultipartBodyBuilder body = new MultipartBodyBuilder();
		
		ByteArrayResource audioResource = new ByteArrayResource(audio.getBytes()) {	
			@Override
            public String getFilename() {
                return audio.getOriginalFilename();
            }
		};
		
		body.part("file", audioResource);
		body.part("model", "gpt-4o-mini-transcribe");
		
		OpenAITranscriptionResponse response = restClient
				.post()
				.uri("/v1/audio/transcriptions")
				.header(
						HttpHeaders.AUTHORIZATION,
						"Bearer " + apiKey
				)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(body.build())
				.retrieve()
				.body(OpenAITranscriptionResponse.class);
		
		if (response == null) {
            throw new IllegalStateException(
                    "OpenAI returned an empty response."
            );
        }
		
		if (response.usage() != null) {
		    globalStatsService.addTokenUsage(
		            response.usage().inputTokens(),
		            response.usage().outputTokens()
		    );
		}
		
		return response.text();
	}
}

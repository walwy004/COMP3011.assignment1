package comp3011.assignment1.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.dto.TranscriptionResponse;
import comp3011.assignment1.service.TranscriptionService;

@RestController
@RequestMapping("/api/v1")
public class TranscriptionController {
	
	private final TranscriptionService transcriptionService;
	
	public TranscriptionController(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }
	
	@PostMapping("/transcriptions")
	public ResponseEntity<TranscriptionResponse> transcribe(
			@RequestParam("audio") MultipartFile audio) throws IOException {
		
		String text = transcriptionService.transcribe(audio);
        
		return ResponseEntity
				.ok()
				.body(new TranscriptionResponse(text));
	}
}

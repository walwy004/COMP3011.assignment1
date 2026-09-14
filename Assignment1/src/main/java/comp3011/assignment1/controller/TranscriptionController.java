package comp3011.assignment1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.dto.TranscriptionResponse;

@RestController
@RequestMapping("/api/v1")
public class TranscriptionController {
	
	@PostMapping("/transcriptions")
	public ResponseEntity<TranscriptionResponse> transcribe(
			@RequestParam("audio") MultipartFile audio) {
		
		System.out.println("Received file: " + audio.getOriginalFilename());
        System.out.println("Size: " + audio.getSize());
		
        TranscriptionResponse response = new TranscriptionResponse("Testing transcription!");
        
		return ResponseEntity
				.ok()
				.body(response);
	}
}

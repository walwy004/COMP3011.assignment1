package comp3011.assignment1.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment1.dto.ErrorResponse;
import comp3011.assignment1.dto.ShutdownResponse;
import comp3011.assignment1.dto.UptimeResponse;
import comp3011.assignment1.service.ShutdownService;
import comp3011.assignment1.service.UptimeService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	
	private final UptimeService uptimeService;
	private final ShutdownService shutdownService;
	
	public AdminController(UptimeService uptimeService, ShutdownService shutdownService) {
		this.uptimeService = uptimeService;
		this.shutdownService = shutdownService;
	}
	
	@GetMapping("/uptime")
	public UptimeResponse getUptime() {
		return uptimeService.getUptime();
	}
	
	@PostMapping("/shutdown")
    public ResponseEntity<?> shutdown() {
		
		boolean accepted = shutdownService.shutdownServer();

        if (!accepted) {
            ErrorResponse error = new ErrorResponse(
                    Instant.now(),
                    409,
                    "Conflict",
                    "Graceful shutdown is already in progress.",
                    "/api/v1/admin/shutdown"
            );

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(error);
        }

        ShutdownResponse response = new ShutdownResponse("Graceful shutdown requested.");

        return ResponseEntity
                .accepted()
                .body(response);
    }
}

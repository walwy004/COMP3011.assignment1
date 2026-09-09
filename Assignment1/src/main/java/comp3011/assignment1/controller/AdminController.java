package comp3011.assignment1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ShutdownResponse> shutdown() {
		
		ShutdownResponse response = shutdownService.shutdown();
		
        return ResponseEntity
        		.accepted()
        		.body(response);
    }
}

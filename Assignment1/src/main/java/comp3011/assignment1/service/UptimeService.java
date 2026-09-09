package comp3011.assignment1.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import comp3011.assignment1.dto.UptimeResponse;

@Service
public class UptimeService {
	
	private final Instant serverStart = Instant.now();
	
	public UptimeResponse getUptime() {
		Instant now = Instant.now();
		double seconds = Duration.between(serverStart, now).toNanos() / 1_000_000_000.0;
		return new UptimeResponse(serverStart, now, seconds);
	}
}

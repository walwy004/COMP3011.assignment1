package comp3011.assignment1.service;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import comp3011.assignment1.dto.ShutdownResponse;

@Service
public class ShutdownService {
	
	private final ConfigurableApplicationContext context;
	
	public ShutdownService(ConfigurableApplicationContext context) {
		this.context = context;
	}
	
	public ShutdownResponse shutdown() {
		
		new Thread(() -> context.close()).start();
		
		return new ShutdownResponse("Graceful shutdown requested.");
	}
	
}

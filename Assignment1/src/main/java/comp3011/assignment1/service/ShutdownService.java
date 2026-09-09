package comp3011.assignment1.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShutdownService {
	
	private final ConfigurableApplicationContext context;
	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
	
	public ShutdownService(ConfigurableApplicationContext context) {
		this.context = context;
	}
	
	public boolean shutdownServer() {

		// If false -> true succeeds, this is the first shutdown request.
        if (!shuttingDown.compareAndSet(false, true)) {
            return false;
        }

        new Thread(() -> context.close()).start();

        return true;
    }
	
}

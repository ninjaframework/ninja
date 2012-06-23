package etc;

import com.google.common.util.concurrent.AbstractIdleService;

public class ServiceImpl extends AbstractIdleService {
	
	protected void startUp() {
		System.out.println("starting up...");
	}

	protected void shutDown() {
		System.out.println("shutting down up...");
	}
}

package layr.routing.api;

import java.util.concurrent.ExecutorService;

public interface ThreadPoolFactory {

	ExecutorService newInstance();

}

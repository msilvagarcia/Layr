package layr.routing.jee;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.routing.api.ThreadPoolFactory;

public class DefaultMethodThreadPoolFactory implements ThreadPoolFactory {

	@Override
	public ExecutorService newInstance() {
		return Executors.newCachedThreadPool();
	}

}

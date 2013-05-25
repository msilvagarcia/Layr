package layr.routing.jee;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.routing.api.ThreadPoolFactory;

public class DefaultRendererThreadPoolFactory implements ThreadPoolFactory {

	@Override
	public ExecutorService newInstance() {
		int availableNumberOfProcessors = Runtime.getRuntime().availableProcessors();
		return Executors.newFixedThreadPool(availableNumberOfProcessors);
	}

}

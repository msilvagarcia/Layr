package layr.routing.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.routing.api.AbstractApplicationContext;
import layr.routing.lifecycle.HandledClass;

public class StubConfiguration extends AbstractApplicationContext {

	private ExecutorService executorService;

	public StubConfiguration() {
		setDefaultResource( "home" );
		executorService = Executors.newFixedThreadPool( 10 );
	}

	@Override
	public Object newInstanceOf(HandledClass routeClass) throws Exception {
		return routeClass.getTargetClass().newInstance();
	}

	@Override
	public ExecutorService getRendererExecutorService() throws Exception {
		return executorService;
	}

	@Override
	public ExecutorService getTaskExecutorService() throws Exception {
		return executorService;
	}


}

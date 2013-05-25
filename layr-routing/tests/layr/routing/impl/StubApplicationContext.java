package layr.routing.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.routing.lifecycle.DefaultApplicationContextImpl;
import layr.routing.lifecycle.HandledClass;

public class StubApplicationContext extends DefaultApplicationContextImpl {

	private ExecutorService executorService;

	public StubApplicationContext() {
		setDefaultResource( "home" );
		executorService = Executors.newCachedThreadPool();
	}

	@Override
	public Object newInstanceOf(HandledClass routeClass) throws Exception {
		return routeClass.getTargetClass().newInstance();
	}

	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}

}

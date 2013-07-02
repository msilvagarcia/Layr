package layr.routing.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.routing.lifecycle.DefaultApplicationContextImpl;

public class StubApplicationContext extends DefaultApplicationContextImpl {

	public StubApplicationContext() {
		setDefaultResource( "home" );
		ExecutorService executorService = Executors.newCachedThreadPool();
		setMethodExecutionThreadPool(executorService);
		setRenderingThreadPool(executorService);
	}
}

package layr.routing.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.routing.lifecycle.DefaultApplicationContextImpl;
import layr.routing.lifecycle.HandledClass;

public class StubApplicationContext extends DefaultApplicationContextImpl {

	public StubApplicationContext() {
		setDefaultResource( "home" );
		//XXX: newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);//
		ExecutorService executorService = Executors.newCachedThreadPool();
		setMethodExecutionThreadPool(executorService);
		setRenderingThreadPool(executorService);
	}

	@Override
	public Object newInstanceOf(HandledClass routeClass) throws Exception {
		return routeClass.getTargetClass().newInstance();
	}

}

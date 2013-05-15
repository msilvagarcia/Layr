package layr.routing.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.engine.RequestContext;
import layr.routing.api.AbstractApplicationContext;
import layr.routing.api.RouteClass;
import layr.routing.service.ContainerRequestData;

public class StubConfiguration extends AbstractApplicationContext {

	private ExecutorService executorService;

	public StubConfiguration() {
		setDefaultResource( "home" );
		executorService = Executors.newFixedThreadPool( 10 );
	}

	@Override
	public RequestContext createContext(ContainerRequestData<?, ?> containerRequestData) {
		StubRequestContext requestContext = new StubRequestContext();
		prePopulateContext( requestContext );
		return requestContext;
	}

	@Override
	public Object newInstanceOf(RouteClass routeClass) throws Exception {
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

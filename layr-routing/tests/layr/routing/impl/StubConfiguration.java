package layr.routing.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import layr.engine.RequestContext;
import layr.routing.api.AbstractConfiguration;
import layr.routing.api.RouteClass;
import layr.routing.exceptions.RoutingException;
import layr.routing.service.ContainerRequestData;

public class StubConfiguration extends AbstractConfiguration {
	
	private ExecutorService executorService;

	public StubConfiguration() {
		setDefaultResource( "home" );
		executorService = Executors.newFixedThreadPool( 5 );
	}

	/* (non-Javadoc)
	 * @see layr.routing.Configuration#createContext()
	 */
	@Override
	public RequestContext createContext( ContainerRequestData<?, ?> containerRequestData ) {
		StubRequestContext requestContext = new StubRequestContext();
		prePopulateContext( requestContext );
		return requestContext;
	}

	/* (non-Javadoc)
	 * @see layr.routing.Configuration#newInstanceOf(layr.routing.RouteClass)
	 */
	@Override
	public Object newInstanceOf( RouteClass routeClass ) throws RoutingException {
		Class<?> targetClass = routeClass.getTargetClass();
		try {
			return targetClass.newInstance();
		} catch (Exception e) {
			throw new RoutingException( "Can't instantiate " + targetClass.getCanonicalName(), e );
		}
	}

	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}

}

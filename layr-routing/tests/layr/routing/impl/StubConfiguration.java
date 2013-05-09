package layr.routing.impl;

import layr.engine.RequestContext;
import layr.routing.AbstractConfiguration;
import layr.routing.ContainerRequestData;
import layr.routing.RouteClass;
import layr.routing.exceptions.RoutingException;

public class StubConfiguration extends AbstractConfiguration {
	
	public StubConfiguration() {
		setDefaultResource( "home" );
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

}

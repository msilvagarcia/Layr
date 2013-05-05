package layr.routing.impl;

import layr.engine.RequestContext;
import layr.routing.AbstractConfiguration;
import layr.routing.RouteClass;
import layr.routing.RoutingException;

public class StubConfiguration extends AbstractConfiguration {

	/* (non-Javadoc)
	 * @see layr.routing.Configuration#createContext()
	 */
	@Override
	public RequestContext createContext() {
		StubRequestContext requestContext = new StubRequestContext();
		requestContext.setCache( getCache() );
		requestContext.getRegisteredTagLibs().putAll( getRegisteredTagLibs() );
		return requestContext;
	}

	/* (non-Javadoc)
	 * @see layr.routing.Configuration#newInstanceOf(layr.routing.RouteClass)
	 */
	@Override
	public Object newInstanceOf( RouteClass routeClass ) throws RoutingException {
		Class<?> targetClass = retrieveTargetClass( routeClass );
		try {
			return targetClass.newInstance();
		} catch (Exception e) {
			throw new RoutingException( "Can't instantiate " + targetClass.getCanonicalName(), e );
		}
	}

}
package layr.routing;

import layr.engine.StubRequestContext;
import layr.engine.RequestContext;

public class StubConfiguration extends AbstractConfiguration {

	/* (non-Javadoc)
	 * @see layr.routing.Configuration#createContext()
	 */
	@Override
	public RequestContext createContext() {
		StubRequestContext requestContext = new StubRequestContext();
		requestContext.setCache( cache );
		requestContext.getRegisteredTagLibs().putAll( registeredTagLibs );
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

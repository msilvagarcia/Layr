package layr.routing.lifecycle;

import java.io.IOException;
import java.util.concurrent.Callable;

import layr.api.RequestContext;
import layr.exceptions.UnhandableException;

public class BusinessRoutingMethodRunner implements Callable<Object> {

    ApplicationContext configuration;
	RequestContext requestContext;
	HandledMethod routeMethod;
	Object instance;

	public BusinessRoutingMethodRunner(
		    ApplicationContext configuration,
			RequestContext requestContext,
			HandledMethod routeMethod,
			Object instance ) {
		this.configuration = configuration;
		this.requestContext = requestContext;
		this.routeMethod = routeMethod;
		this.instance = instance;
	}

	public Object call() throws Exception {
		Request routingRequest = createRoutingRequest( routeMethod );
		try {
			return runMethod( routingRequest, routeMethod );
		} catch (Throwable e) {
			if ( e instanceof Exception )
				throw (Exception)e;
			throw new UnhandableException(e);
		}
	}

	public Request createRoutingRequest(HandledMethod routeMethod) throws IOException {
    	return new Request( configuration, requestContext, routeMethod.getRouteMethodPattern() );
    }

	public Object runMethod(Request routingRequest, HandledMethod routeMethod) throws Throwable {
		return routeMethod.invoke( routingRequest, instance );
	}
}

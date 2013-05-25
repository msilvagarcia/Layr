package layr.routing.lifecycle;

import java.util.concurrent.Callable;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.Response;
import layr.routing.exceptions.UnhandableException;

public class BusinessRoutingMethodRunner implements Callable<Response> {

    ApplicationContext configuration;
	RequestContext requestContext;
	HandledMethod routeMethod;

	public BusinessRoutingMethodRunner(
		    ApplicationContext configuration,
			RequestContext requestContext,
			HandledMethod routeMethod ) {
		this.configuration = configuration;
		this.requestContext = requestContext;
		this.routeMethod = routeMethod;
	}

	public Response call() throws Exception {
		Request routingRequest = createRoutingRequest( routeMethod );
		try {
			return runMethod( routingRequest, routeMethod );
		} catch (Throwable e) {
			if ( e instanceof Exception )
				throw (Exception)e;
			throw new UnhandableException(e);
		}
	}

	public Request createRoutingRequest(HandledMethod routeMethod) {
    	return new Request( configuration, requestContext, routeMethod.getRouteMethodPattern() );
    }

	public Response runMethod(Request routingRequest, HandledMethod routeMethod) throws Throwable {
		HandledClass routeClass = routeMethod.getRouteClass();
		Object instance = configuration.newInstanceOf( routeClass );
		routeMethod.invoke( routingRequest, instance );
		return createRoutingResponse(instance, routeMethod);
	}

	public Response createRoutingResponse(Object instance, HandledMethod routeMethod) {
		if ( routeMethod.getLastReturnedValue() != null
		&&   routeMethod.getLastReturnedValue() instanceof Response )
			return (Response)routeMethod.getLastReturnedValue();
		return null;
	}

}

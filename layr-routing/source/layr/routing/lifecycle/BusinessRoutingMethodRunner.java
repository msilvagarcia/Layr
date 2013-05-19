package layr.routing.lifecycle;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.Response;
import layr.routing.exceptions.UnhandledException;

public class BusinessRoutingMethodRunner {

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

	public Response run() throws UnhandledException {
		Request routingRequest = createRoutingRequest( routeMethod );
		try {
			return runMethod( routingRequest, routeMethod );
		} catch ( Throwable e ) {
			return handleException( e );
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Throwable> Response handleException(T e) throws UnhandledException {
		String canonicalName = e.getClass().getCanonicalName();
		Class<ExceptionHandler> exceptionHandlerClass = configuration.getRegisteredExceptionHandlers().get( canonicalName );
		if ( exceptionHandlerClass == null )
			throw new UnhandledException( e );
		try {
			ExceptionHandler<?> exceptionHandlerInstance = exceptionHandlerClass.newInstance();
			return ((ExceptionHandler<T>)exceptionHandlerInstance).render( e );
		} catch (Throwable e1) {
			throw new UnhandledException( e1 );
		}
	}

}

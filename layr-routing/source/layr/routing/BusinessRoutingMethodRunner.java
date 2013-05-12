package layr.routing;

import layr.commons.Reflection;
import layr.engine.RequestContext;
import layr.routing.annotations.Route;
import layr.routing.exceptions.RoutingException;
import layr.routing.exceptions.UnhandledException;

public class BusinessRoutingMethodRunner {

    Configuration configuration;
	RequestContext requestContext;
	RouteMethod routeMethod;
	
	public BusinessRoutingMethodRunner(
		    Configuration configuration,
			RequestContext requestContext,
			RouteMethod routeMethod ) {
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
	
	@SuppressWarnings("unchecked")
	public <T extends Throwable> Response handleException(T e) throws UnhandledException {
		String canonicalName = e.getClass().getCanonicalName();
		Class<ExceptionHandler<?>> exceptionHandlerClass = configuration.getRegisteredExceptionHandlers().get( canonicalName );
		if ( exceptionHandlerClass == null )
			throw new UnhandledException( e );
		
		try {
			ExceptionHandler<?> exceptionHandlerInstance = exceptionHandlerClass.newInstance();
			return ((ExceptionHandler<T>)exceptionHandlerInstance).render( e );
		} catch (Throwable e1) {
			throw new UnhandledException( e1 );
		}
	}

    public Request createRoutingRequest(RouteMethod routeMethod) {
    	return new Request( requestContext, routeMethod.getRouteMethodPattern() );
    }

	public Response runMethod(Request routingRequest, RouteMethod routeMethod) throws Throwable {
		RouteClass routeClass = routeMethod.routeClass;
		Object instance = configuration.newInstanceOf( routeClass );
		populateWithParameters( instance, routeClass, routingRequest );
		routeMethod.invoke( routingRequest, instance );
		populateRequestContextWithInstanceTemplateParameters( instance, routeClass );
		return createRoutingResponse(instance, routeMethod);
	}

	public void populateWithParameters(Object instance, RouteClass routeClass, Request routingRequest) throws RoutingException {
		for ( RouteParameter parameter : routeClass.getParameters())
			if ( !( parameter instanceof TemplateRouteParameter ) )
				populateWithParameter( instance, routeClass, routingRequest, parameter );
	}

	public void populateWithParameter(
			Object instance, RouteClass routeClass,
			Request routingRequest, RouteParameter parameter) throws RoutingException {
		Object value = null;
		try {
			value = routingRequest.getValue( parameter );
			Reflection.setAttribute( instance, parameter.name, value );
		} catch (Exception e) {
			String message = String.format(
				"[WARN] Can't set the value '%s' to %s.%s: %s",
				value, routeClass.targetClass.getCanonicalName(),
				parameter.name, e.getMessage());
			requestContext.log( message );
			throw new RoutingException( message, e );
		}
	}

	public void populateRequestContextWithInstanceTemplateParameters(Object instance, RouteClass routeClass) {
		Object value;
		for ( RouteParameter parameter : routeClass.getParameters()){
			if ( !(parameter instanceof TemplateRouteParameter) )
				continue;
			value = Reflection.getAttribute( instance, parameter.name );
			requestContext.put( parameter.name, value );
		}
	}

	public Response createRoutingResponse(Object instance, RouteMethod routeMethod) {
		if ( routeMethod.lastReturnedValue != null
		&&   routeMethod.lastReturnedValue instanceof Response )
			return (Response)routeMethod.lastReturnedValue;

		Route annotation = routeMethod.getRouteAnnotation();
		Response response = new Response();
		response.template = annotation.template();
		response.redirectTo = annotation.redirectTo();
		return response;
	}

}

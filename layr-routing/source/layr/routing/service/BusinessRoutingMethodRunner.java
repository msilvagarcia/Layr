package layr.routing.service;

import layr.commons.Reflection;
import layr.engine.RequestContext;
import layr.routing.annotations.Route;
import layr.routing.api.Configuration;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.Request;
import layr.routing.api.Response;
import layr.routing.api.RouteClass;
import layr.routing.api.RouteMethod;
import layr.routing.api.RouteParameter;
import layr.routing.api.TemplateRouteParameter;
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
		RouteClass routeClass = routeMethod.getRouteClass();
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
			Reflection.setAttribute( instance, parameter.getName(), value );
		} catch (Exception e) {
			String message = String.format(
				"[WARN] Can't set the value '%s' to %s.%s: %s",
				value, routeClass.getTargetClass().getCanonicalName(),
				parameter.getName(), e.getMessage());
			requestContext.log( message );
			throw new RoutingException( message, e );
		}
	}

	public void populateRequestContextWithInstanceTemplateParameters(Object instance, RouteClass routeClass) {
		Object value;
		for ( RouteParameter parameter : routeClass.getParameters()){
			if ( !(parameter instanceof TemplateRouteParameter) )
				continue;
			value = Reflection.getAttribute( instance, parameter.getName() );
			requestContext.put( parameter.getName(), value );
		}
	}

	public Response createRoutingResponse(Object instance, RouteMethod routeMethod) {
		if ( routeMethod.getLastReturnedValue() != null
		&&   routeMethod.getLastReturnedValue() instanceof Response )
			return (Response)routeMethod.getLastReturnedValue();

		Route annotation = routeMethod.getRouteAnnotation();
		Response response = new Response();
		response.renderTemplate( annotation.template() );
		response.redirectTo( annotation.redirectTo() );
		return response;
	}

}

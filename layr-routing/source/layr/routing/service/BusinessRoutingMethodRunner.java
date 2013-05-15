package layr.routing.service;

import layr.commons.Reflection;
import layr.engine.RequestContext;
import layr.routing.annotations.Route;
import layr.routing.api.ApplicationContext;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.Request;
import layr.routing.api.Response;
import layr.routing.api.HandledClass;
import layr.routing.api.HandledMethod;
import layr.routing.api.HandledParameter;
import layr.routing.api.TemplateHandledParameter;
import layr.routing.exceptions.RoutingException;
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

    public Request createRoutingRequest(HandledMethod routeMethod) {
    	return new Request( requestContext, routeMethod.getRouteMethodPattern() );
    }

	public Response runMethod(Request routingRequest, HandledMethod routeMethod) throws Throwable {
		HandledClass routeClass = routeMethod.getRouteClass();
		Object instance = configuration.newInstanceOf( routeClass );
		populateWithParameters( instance, routeClass, routingRequest );
		routeMethod.invoke( routingRequest, instance );
		populateRequestContextWithInstanceTemplateParameters( instance, routeClass );
		return createRoutingResponse(instance, routeMethod);
	}

	public void populateWithParameters(Object instance, HandledClass routeClass, Request routingRequest) throws RoutingException {
		for ( HandledParameter parameter : routeClass.getParameters())
			if ( !( parameter instanceof TemplateHandledParameter ) )
				populateWithParameter( instance, routeClass, routingRequest, parameter );
	}

	public void populateWithParameter(
			Object instance, HandledClass routeClass,
			Request routingRequest, HandledParameter parameter) throws RoutingException {
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

	public void populateRequestContextWithInstanceTemplateParameters(Object instance, HandledClass routeClass) {
		Object value;
		for ( HandledParameter parameter : routeClass.getParameters()){
			if ( !(parameter instanceof TemplateHandledParameter) )
				continue;
			value = Reflection.getAttribute( instance, parameter.getName() );
			requestContext.put( parameter.getName(), value );
		}
	}

	public Response createRoutingResponse(Object instance, HandledMethod routeMethod) {
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

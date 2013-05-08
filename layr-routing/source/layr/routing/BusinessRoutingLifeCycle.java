package layr.routing;

import static layr.commons.StringUtil.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import layr.commons.Reflection;
import layr.engine.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.components.Component;
import layr.engine.components.TemplateParsingException;
import layr.routing.annotations.Route;
import layr.routing.exceptions.NotFoundException;
import layr.routing.exceptions.RoutingException;
import layr.routing.exceptions.UnhandledException;

public class BusinessRoutingLifeCycle implements LifeCycle {

    Configuration configuration;
	RequestContext requestContext;

	public BusinessRoutingLifeCycle(Configuration configuration) {
    	this.configuration = configuration;
	}

	public void createContext( ContainerRequestData<?, ?> containerRequestData ) {
		this.requestContext = configuration.createContext( containerRequestData );
	}

    public void run() throws NotFoundException, RoutingException, UnhandledException {
    	for ( RouteClass resource : configuration.getRegisteredWebResources() )
    		if ( resource.matchesTheRequestURI( requestContext ) )
		    	for ( RouteMethod routeMethod : resource.getRouteMethods() )
		    		if ( routeMethod.matchesTheRequest( requestContext ) ) {
		    			// TODO: Colocar metodo na cache para evitar que URL's já visitados precisem passar por esta rotina
		    			runMethodAndRenderOutput( routeMethod );
		    			return;
		    		}
    	throw new NotFoundException( "No route found." );
    }

	public void runMethodAndRenderOutput(RouteMethod routeMethod) throws RoutingException, UnhandledException {
		Request routingRequest = createRoutingRequest( routeMethod );
		Response routingResponse = null;
		try {
			routingResponse = runMethod( routingRequest, routeMethod );
		} catch ( Throwable e ) {
			routingResponse = handleException( e );
		}
		renderOutput(routingResponse);
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

	public void renderOutput( Response response ) throws RoutingException {
		try {
			if ( !isEmpty( response.redirectTo ) )
				requestContext.redirectTo( response.redirectTo );
			else if ( !isEmpty( response.template ) )
				render( response );
			else
				responseNoContent();
		} catch ( IOException e ) {
			throw new RoutingException( e );
		}
	}

	public void render( Response response ) throws RoutingException {
		try {
			setContentTypeAndEncoding( response );
			TemplateParser parser = new TemplateParser( requestContext );
			Component compiledTemplate = parser.compile( response.template );
			compiledTemplate.render();
		} catch (TemplateParsingException e) {
			throw new RoutingException( "Can't to compile '" + response.template + "' template.", e );
		} catch (IOException e) {
			throw new RoutingException( "Can't to render '" + response.template + "' template.", e );
		}
	}

	public void setContentTypeAndEncoding( Response response ) throws UnsupportedEncodingException {
		requestContext.setContentType( "text/html" );
		requestContext.setCharacterEncoding(
			oneOf( response.encoding, configuration.getDefaultEncoding() ) );
	}

	public void responseNoContent() {
		requestContext.setStatusCode( 204 );
	}
	
	public RequestContext getRequestContext() {
		return requestContext;
	}
}

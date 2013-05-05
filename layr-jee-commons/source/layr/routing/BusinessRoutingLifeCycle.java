package layr.routing;

import java.io.IOException;

import static layr.commons.StringUtil.*;
import layr.commons.Reflection;
import layr.engine.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.components.Component;
import layr.engine.components.TemplateParsingException;
import layr.routing.annotations.Route;

public class BusinessRoutingLifeCycle implements LifeCycle {

    Configuration configuration;
	RequestContext requestContext;

	public BusinessRoutingLifeCycle(Configuration configuration) {
    	this.configuration = configuration;
    	this.requestContext = configuration.createContext();
	}

    public void run() throws NotFoundException, RoutingException {
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

	public void runMethodAndRenderOutput(RouteMethod routeMethod) throws RoutingException {
		Request routingRequest = createRoutingRequest( routeMethod );
		Response routingResponse = runMethod( routingRequest, routeMethod );
		renderOutput(routingResponse);
	}

    public Request createRoutingRequest(RouteMethod routeMethod) {
    	return new Request( requestContext, routeMethod.getRouteMethodPattern() );
    }

	public Response runMethod(Request routingRequest, RouteMethod routeMethod) throws RoutingException {
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
		if ( routeMethod.lastReturnedValue instanceof Response )
			return (Response)routeMethod.lastReturnedValue;

		Route annotation = routeMethod.getRouteAnnotation();
		Response response = new Response();
		response.template = annotation.template();
		response.redirectTo = annotation.redirectTo();
		return response;
	}

	public void renderOutput( Response response ) throws RoutingException {
		if ( !isEmpty( response.redirectTo ) )
			requestContext.redirectTo( response.redirectTo );
		else if ( !isEmpty( response.template ) )
			render( response );
		else
			responseNoContent();
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

	public void setContentTypeAndEncoding( Response response ) {
		requestContext.setContentType( "text/html" );
		requestContext.setCharacterEncoding( response.encoding );
	}

	public void responseNoContent() {
		requestContext.setStatusCode( 204 );
	}
	
	public RequestContext getRequestContext() {
		return requestContext;
	}
}

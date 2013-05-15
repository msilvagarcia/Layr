package layr.routing.service;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.Response;
import layr.routing.api.RouteMethod;
import layr.routing.exceptions.NotFoundException;
import layr.routing.exceptions.RoutingException;
import layr.routing.exceptions.UnhandledException;

public class BusinessRoutingLifeCycle implements LifeCycle {

    ApplicationContext configuration;
	RequestContext requestContext;

	public BusinessRoutingLifeCycle(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
	}

    public void run() throws NotFoundException, RoutingException, UnhandledException {
    	RouteMethod routeMethod = getMatchedRouteMethod();
    	if ( routeMethod == null )
    		throw new NotFoundException( "No route found." );
		runMethodAndRenderOutput( routeMethod );
    }

	public RouteMethod getMatchedRouteMethod() {
		BusinessRoutingMethodMatching businessRoutingMethodMatching = new BusinessRoutingMethodMatching( configuration, requestContext );
		RouteMethod routeMethod = businessRoutingMethodMatching.getMatchedRouteMethod();
		return routeMethod;
	}

	public void runMethodAndRenderOutput(RouteMethod routeMethod) throws RoutingException, UnhandledException {
		Response response = runMethod( routeMethod );
		renderOutput( response );
	}

	public void renderOutput(Response response) throws RoutingException {
		BusinessRoutingRenderer renderer = new BusinessRoutingRenderer( configuration, requestContext );
		renderer.render(response);
	}

	public Response runMethod(RouteMethod routeMethod) throws UnhandledException {
		BusinessRoutingMethodRunner runner = new BusinessRoutingMethodRunner( configuration, requestContext, routeMethod );
		Response response = runner.run();
		return response;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}
}

package layr.routing.service;

import layr.engine.RequestContext;
import layr.routing.api.Configuration;
import layr.routing.api.Response;
import layr.routing.api.RouteMethod;
import layr.routing.exceptions.RoutingException;
import layr.routing.exceptions.UnhandledException;

public class BusinessRoutingAsyncRunner {

	Configuration configuration;
	RequestContext requestContext;

	public BusinessRoutingAsyncRunner(
			Configuration configuration,
			RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
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
}

package layr.routing;

import layr.engine.RequestContext;

public class BusinessRoutingMethodMatching {

	Configuration configuration;
	RequestContext requestContext;

	public BusinessRoutingMethodMatching(Configuration configuration, RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
	}

	public RouteMethod getMatchedRouteMethod() {
		for (RouteClass resource : configuration.getRegisteredWebResources())
			if (resource.matchesTheRequestURI( requestContext ))
				for (RouteMethod routeMethod : resource.getRouteMethods())
					if (routeMethod.matchesTheRequest( requestContext )) {
						// TODO: Colocar metodo na cache para evitar que URL's
						// já visitados precisem passar por esta rotina
						return routeMethod;
					}
		return null;
	}
}

package layr.routing.service;

import layr.engine.RequestContext;
import layr.routing.api.Configuration;
import layr.routing.api.RouteClass;
import layr.routing.api.RouteMethod;

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

package layr.routing.lifecycle;

import layr.api.RequestContext;

public class BusinessRoutingMethodMatching {

	ApplicationContext configuration;
	RequestContext requestContext;

	public BusinessRoutingMethodMatching(ApplicationContext configuration, RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
	}

	public HandledMethod getMatchedRouteMethod() {
		for (HandledClass resource : configuration.getRegisteredWebResources())
			if (resource.matchesTheRequestURI( requestContext ))
				for (HandledMethod routeMethod : resource.getRouteMethods())
					if (routeMethod.matchesTheRequest( requestContext )) {
						return routeMethod;
					}
		return null;
	}
}

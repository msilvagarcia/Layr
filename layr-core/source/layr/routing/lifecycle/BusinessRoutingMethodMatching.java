package layr.routing.lifecycle;

import java.util.List;

import layr.api.ApplicationContext;
import layr.api.RequestContext;

public class BusinessRoutingMethodMatching {

	ApplicationContext configuration;
	RequestContext requestContext;

	public BusinessRoutingMethodMatching(ApplicationContext configuration,
			RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
	}

	public HandledMethod getMatchedRouteMethod() {
		for (HandledClass resource : getRegisteredWebResources())
			if (resource.matchesTheRequestURI(requestContext))
				for (HandledMethod routeMethod : resource.getRouteMethods())
					if (routeMethod.matchesTheRequest(requestContext)) {
						return routeMethod;
					}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<HandledClass> getRegisteredWebResources() {
		List<HandledClass> registeredWebResources = (List<HandledClass>) configuration
				.getAttribute(HandledClass.class.getCanonicalName());
		return registeredWebResources;
	}
}

package layr.routing.jee;

import layr.engine.RequestContext;
import layr.routing.AbstractConfiguration;
import layr.routing.RouteClass;
import layr.routing.exceptions.RoutingException;

public class JEEConfiguration extends AbstractConfiguration {

	EnterpriseJavaBeansContext ejbContext;

	@Override
	public RequestContext createContext() {
		return null;
	}

	@Override
	public Object newInstanceOf(RouteClass routeClass) throws RoutingException {
		return null;
	}

	public EnterpriseJavaBeansContext getEjbContext() {
		return ejbContext;
	}

	public void setEjbContext(EnterpriseJavaBeansContext ejbContext) {
		this.ejbContext = ejbContext;
	}

}

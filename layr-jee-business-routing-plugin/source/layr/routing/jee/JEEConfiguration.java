package layr.routing.jee;

import javax.servlet.ServletContext;


import layr.engine.RequestContext;
import layr.routing.AbstractConfiguration;
import layr.routing.RouteClass;
import layr.routing.RoutingException;

public class JEEConfiguration extends AbstractConfiguration {
	
	ServletContext servletContext;
	EnterpriseJavaBeansContext ejbContext;

	@Override
	public RequestContext createContext() {
		return null;
	}

	@Override
	public Object newInstanceOf(RouteClass routeClass) throws RoutingException {
		return null;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public EnterpriseJavaBeansContext getEjbContext() {
		return ejbContext;
	}

	public void setEjbContext(EnterpriseJavaBeansContext ejbContext) {
		this.ejbContext = ejbContext;
	}

}

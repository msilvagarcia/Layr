package layr.routing.jee;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.engine.RequestContext;
import layr.routing.AbstractConfiguration;
import layr.routing.ContainerRequestData;
import layr.routing.RouteClass;
import layr.routing.exceptions.RoutingException;

public class JEEConfiguration extends AbstractConfiguration {

	EnterpriseJavaBeansContext ejbContext;

	@SuppressWarnings("unchecked")
	@Override
	public RequestContext createContext( ContainerRequestData<?, ?> containerRequestData ) {
		ContainerRequestData<HttpServletRequest, HttpServletResponse> jeeRequestData =
				(ContainerRequestData<HttpServletRequest, HttpServletResponse>) containerRequestData;
		JEERequestContext jeeRequestContext = new JEERequestContext( jeeRequestData.getRequest(), jeeRequestData.getResponse() );
		jeeRequestContext.setCache( getCache() );
		jeeRequestContext.getRegisteredTagLibs().putAll( getRegisteredTagLibs() );
		return jeeRequestContext;
	}

	@Override
	public Object newInstanceOf(RouteClass routeClass) throws RoutingException {
		Class<?> targetClass = routeClass.getTargetClass();
		try {
			Object instance = ejbContext.lookup( targetClass );
			if ( instance != null )
				return instance;
			return newInstanceFromReflection(targetClass);
		} catch (NamingException e) {
			throw new RoutingException( "Can't instantiate " + targetClass.getCanonicalName(), e );
		}
	}
	
	public Object newInstanceFromReflection( Class<?> targetClass ) throws RoutingException {
		try {
			return targetClass.newInstance();
		} catch (Exception e) {
			throw new RoutingException( "Can't instantiate " + targetClass.getCanonicalName(), e );
		}
	}

	public EnterpriseJavaBeansContext getEjbContext() {
		return ejbContext;
	}

	public void setEjbContext(EnterpriseJavaBeansContext ejbContext) {
		this.ejbContext = ejbContext;
	}

}

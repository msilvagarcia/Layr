package layr.routing.jee;

import java.util.concurrent.ExecutorService;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.engine.RequestContext;
import layr.routing.api.AbstractApplicationContext;
import layr.routing.exceptions.RoutingException;
import layr.routing.lifecycle.ContainerRequestData;
import layr.routing.lifecycle.HandledClass;

class JEEConfiguration extends AbstractApplicationContext {

	EnterpriseJavaBeansContext ejbContext;

	@SuppressWarnings("unchecked")
	@Override
	public RequestContext createContext( ContainerRequestData<?, ?> containerRequestData ) {
		ContainerRequestData<HttpServletRequest, HttpServletResponse> jeeRequestData =
				(ContainerRequestData<HttpServletRequest, HttpServletResponse>) containerRequestData;
		JEERequestContext jeeRequestContext = new JEERequestContext( jeeRequestData.getRequest(), jeeRequestData.getResponse() );
		prePopulateContext( jeeRequestContext );
		return jeeRequestContext;
	}

	@Override
	public Object newInstanceOf(HandledClass routeClass) throws RoutingException {
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

	@Override
	public ExecutorService getRendererExecutorService() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecutorService getTaskExecutorService() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

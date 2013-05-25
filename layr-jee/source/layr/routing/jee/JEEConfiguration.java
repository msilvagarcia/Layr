package layr.routing.jee;

import javax.naming.NamingException;

import layr.exceptions.RoutingException;
import layr.routing.lifecycle.DefaultApplicationContextImpl;
import layr.routing.lifecycle.HandledClass;

class JEEConfiguration extends DefaultApplicationContextImpl {

	EnterpriseJavaBeansContext ejbContext;

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

}

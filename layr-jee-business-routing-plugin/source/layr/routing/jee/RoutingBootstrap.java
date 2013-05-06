package layr.routing.jee;

import javax.naming.NamingException;
import javax.servlet.ServletContext;

import layr.routing.Configuration;


public class RoutingBootstrap extends layr.routing.RoutingBootstrap {

	EnterpriseJavaBeansContext ejbContext;
	ServletContext servletContext;

	public RoutingBootstrap( ServletContext servletContext ) throws NamingException {
		super();
		this.ejbContext = new EnterpriseJavaBeansContext();
		this.servletContext = servletContext;
	}

	@Override
	public void analyse(Class<?> clazz) throws Exception {
		super.analyse( clazz );
		ejbContext.registerAnnotatedEJBViewsInterfaces( clazz );
	}

	@Override
	public Configuration createConfiguration() {
		JEEConfiguration configuration = new JEEConfiguration();
		configuration.setEjbContext( ejbContext );
		configuration.setServletContext( servletContext );
		configuration.setRegisteredTagLibs( getRegisteredTagLibs() );
		configuration.setRegisteredWebResources( getRegisteredWebResources() );
		configuration.setRegisteredExceptionHandlers( getExceptionHandlers() );
		return configuration;
	}
}

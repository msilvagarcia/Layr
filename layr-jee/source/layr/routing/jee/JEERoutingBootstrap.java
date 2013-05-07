package layr.routing.jee;

import javax.naming.NamingException;
import javax.servlet.ServletContext;

import layr.routing.Configuration;
import layr.routing.RoutingBootstrap;

public class JEERoutingBootstrap extends RoutingBootstrap {

	EnterpriseJavaBeansContext ejbContext;
	ServletContext servletContext;

	public JEERoutingBootstrap( ServletContext servletContext ) throws NamingException {
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
		configuration.setRegisteredTagLibs( getRegisteredTagLibs() );
		configuration.setRegisteredWebResources( getRegisteredWebResources() );
		configuration.setRegisteredExceptionHandlers( getExceptionHandlers() );
		return configuration;
	}
}

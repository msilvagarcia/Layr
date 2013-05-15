package layr.routing.jee;

import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import layr.engine.Cache;
import layr.engine.components.TagLib;
import layr.routing.annotations.WebResource;
import layr.routing.api.ApplicationContext;
import layr.routing.exceptions.RoutingInitializationException;
import layr.routing.service.RoutingBootstrap;

@HandlesTypes({
	TagLib.class,
	WebResource.class,
	Stateless.class,
	Stateful.class,
	Singleton.class})
public class JEERoutingBootstrap extends RoutingBootstrap implements javax.servlet.ServletContainerInitializer {

	EnterpriseJavaBeansContext ejbContext;

	public JEERoutingBootstrap() throws NamingException {
		super();
		this.ejbContext = new EnterpriseJavaBeansContext();
	}

	@Override
	public void analyse(Class<?> clazz) throws Exception {
		super.analyse( clazz );
		ejbContext.seekForJNDIEJBViewsfor( clazz );
	}

	@Override
	public ApplicationContext createConfiguration() {
		JEEConfiguration configuration = new JEEConfiguration();
		configuration.setEjbContext( ejbContext );
		configuration.setCache( getCache() );
		configuration.setDefaultResource( getDefaultResource() );
		configuration.setDefaultEncoding( getDefaultEncoding() );
		configuration.setRegisteredTagLibs( getRegisteredTagLibs() );
		configuration.setRegisteredWebResources( getRegisteredWebResources() );
		configuration.setRegisteredExceptionHandlers( getExceptionHandlers() );
		return configuration;
	}
	
	public Cache getCache(){
		if ( System.getProperty( "layr.routing.cacheable", "false" ).equals( "true" ) )
			return new Cache();
		return null;
	}
	
	public String getDefaultEncoding(){
		return System.getProperty( "layr.routing.encoding", "UTF-8" );
	}
	
	public String getDefaultResource(){
		return System.getProperty( "layr.routing.home", "home" );
	}

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
		try {
			analyse( classes );
			ApplicationContext configuration = createConfiguration();
			ctx.setAttribute( JEEConfiguration.class.getCanonicalName(), configuration );
		} catch (RoutingInitializationException e) {
			throw new ServletException( e );
		}
	}
}

package layr.routing.jee;

import static layr.commons.StringUtil.isEmpty;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import layr.engine.Cache;
import layr.engine.components.TagLib;
import layr.routing.api.ApplicationContext;
import layr.routing.api.ThreadPoolFactory;
import layr.routing.api.WebResource;
import layr.routing.exceptions.RoutingInitializationException;
import layr.routing.lifecycle.RoutingBootstrap;

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
	public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
		try {
			ApplicationContext configuration = configure( classes );
			ctx.setAttribute( JEEConfiguration.class.getCanonicalName(), configuration );
		} catch (RoutingInitializationException e) {
			throw new ServletException( e );
		}
	}

	@Override
	public void analyse(Class<?> clazz) throws Exception {
		super.analyse( clazz );
		ejbContext.seekForJNDIEJBViewsfor( clazz );
	}

	@Override
	public ApplicationContext createConfiguration() throws RoutingInitializationException {
		try {
			JEEConfiguration configuration = new JEEConfiguration();
			configuration.setEjbContext( ejbContext );
			configuration.setCache( getCache() );
			configuration.setDefaultResource( getDefaultResource() );
			configuration.setDefaultEncoding( getDefaultEncoding() );
			configuration.setRegisteredTagLibs( getRegisteredTagLibs() );
			configuration.setRegisteredWebResources( getRegisteredWebResources() );
			configuration.setRegisteredExceptionHandlers( getRegisteredExceptionHandlers() );
			configuration.setRegisteredDataProviders(getRegisteredDataProviders());
			configuration.setMethodExecutionThreadPool(getMethodExecutionThreadPool());
			configuration.setRenderingThreadPool(getRenderingThreadPool());
			return configuration;
		} catch ( Throwable e ){
			throw new RoutingInitializationException(e);
		}
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
	
	public ExecutorService getMethodExecutionThreadPool() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class<?> clazz = getClassFromProperty("layr.pool.methods", DefaultMethodThreadPoolFactory.class);
		ThreadPoolFactory poolFactory = (ThreadPoolFactory) clazz.newInstance();
		return (ExecutorService)poolFactory.newInstance();
	}
	
	public ExecutorService getRenderingThreadPool() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class<?> clazz = getClassFromProperty("layr.pool.rendering", DefaultRendererThreadPoolFactory.class);
		ThreadPoolFactory poolFactory = (ThreadPoolFactory) clazz.newInstance();
		return (ExecutorService)poolFactory.newInstance();
	}
	
	public Class<?> getClassFromProperty( String propertyName, Class<?> defaultClass )
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = System.getProperty(propertyName);
		if ( isEmpty( className ) )
			return defaultClass;
		return Class.forName(className);
	}
}

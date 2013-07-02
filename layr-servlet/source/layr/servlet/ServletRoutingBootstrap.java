package layr.servlet;

import static layr.commons.StringUtil.isEmpty;

import java.util.concurrent.ExecutorService;

import javax.servlet.ServletContext;

import layr.api.ApplicationContext;
import layr.api.Cache;
import layr.api.ThreadPoolFactory;
import layr.exceptions.RoutingInitializationException;
import layr.routing.lifecycle.RoutingBootstrap;

public class ServletRoutingBootstrap extends RoutingBootstrap {

	ServletContext servletContext;

	public ServletRoutingBootstrap( ServletContext servletContext ) {
		this.servletContext = servletContext;
	}

	@Override
	public ApplicationContext createConfiguration() throws RoutingInitializationException {
		try {
			ServletApplicationContext configuration = new ServletApplicationContext( servletContext );
			configuration.setCache( getCache() );
			configuration.setDefaultResource( getDefaultResource() );
			configuration.setDefaultEncoding( getDefaultEncoding() );
			configuration.setRegisteredTagLibs( getRegisteredTagLibs() );
			configuration.setRegisteredWebResources( getRegisteredWebResources() );
			configuration.setRegisteredExceptionHandlers( getRegisteredExceptionHandlers() );
			configuration.setRegisteredDataProviders(getRegisteredDataProviders());
			configuration.setRegisteredClassFactories(getRegisteredClassFactories());
			configuration.setMethodExecutionThreadPool(getMethodExecutionThreadPool());
			configuration.setRenderingThreadPool(getRenderingThreadPool());
			configuration.setRegisteredOutputRenderes(getRegisteredOutputRenderers());
			configuration.setRegisteredInputConverter(getRegisteredInputConverter());
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

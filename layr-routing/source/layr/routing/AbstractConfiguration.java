package layr.routing;

import java.util.List;
import java.util.Map;

import layr.engine.AbstractRequestContext;
import layr.engine.Cache;
import layr.engine.components.ComponentFactory;

/**
 * Default abstract implementation of Configuration interface. Developers
 * are encouraged to extends this class in order to avoid rework.
 */
public abstract class AbstractConfiguration implements Configuration {

	String defaultResource;
	Cache cache;
	Map<String, ComponentFactory> registeredTagLibs;
	Map<String, Class<ExceptionHandler<?>>> registeredExceptionHandlers;
	List<RouteClass> exposedResources;
	String defaultEncoding;

	@Override
	public String getDefaultResource() {
		return defaultResource;
	}

	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}

	@Override
	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public void setRegisteredTagLibs(Map<String, ComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public List<RouteClass> getRegisteredWebResources() {
		return exposedResources;
	}

	public void setRegisteredWebResources(List<RouteClass> exposedMethods) {
		this.exposedResources = exposedMethods;
	}

	public Map<String, Class<ExceptionHandler<?>>> getRegisteredExceptionHandlers() {
		return registeredExceptionHandlers;
	}

	public void setRegisteredExceptionHandlers(Map<String, Class<ExceptionHandler<?>>> registeredExceptionHandlers) {
		this.registeredExceptionHandlers = registeredExceptionHandlers;
	}
	
	public String getDefaultEncoding() {
		return defaultEncoding;
	}
	
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
	
	public void prePopulateContext( AbstractRequestContext requestContext ) {
		requestContext.setCache( getCache() );
		requestContext.setRegisteredTagLibs( getRegisteredTagLibs() );
		requestContext.setDefaultResource( getDefaultResource() );
	}
}

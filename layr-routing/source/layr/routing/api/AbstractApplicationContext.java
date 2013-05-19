package layr.routing.api;

import java.util.List;
import java.util.Map;

import layr.engine.Cache;
import layr.engine.components.ComponentFactory;
import layr.routing.lifecycle.HandledClass;

/**
 * Default abstract implementation of Configuration interface. Developers
 * are encouraged to extends this class in order to avoid rework.
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

	String defaultResource;
	Cache cache;
	List<HandledClass> registeredWebResources;
	List<HandledClass> registeredTemplateParameterObject;
	Map<String, ComponentFactory> registeredTagLibs;
	Map<String, Class<ExceptionHandler<?>>> registeredExceptionHandlers;
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
	public List<HandledClass> getRegisteredWebResources() {
		return registeredWebResources;
	}

	public void setRegisteredWebResources(List<HandledClass> exposedMethods) {
		this.registeredWebResources = exposedMethods;
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

}

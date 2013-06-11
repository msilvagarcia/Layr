package layr.routing.lifecycle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import layr.api.Cache;
import layr.api.ComponentFactory;
import layr.api.DataProvider;
import layr.api.ExceptionHandler;

/**
 * Default implementation of ApplicationContext interface. Developers
 * are encouraged to extends this class in order to avoid rework.
 */
public class DefaultApplicationContextImpl implements ApplicationContext {

	Cache cache;
	String defaultResource;
	String defaultEncoding;
	ExecutorService renderingThreadPool;
	ExecutorService methodExecutionThreadPool;
	List<HandledClass> registeredWebResources;
	List<HandledClass> registeredTemplateParameterObject;
	Map<String, ComponentFactory> registeredTagLibs;

	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends ExceptionHandler>> registeredExceptionHandlers;
	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends DataProvider>> registeredDataProviders;

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

	@SuppressWarnings("rawtypes")
	public Map<String, Class<? extends ExceptionHandler>> getRegisteredExceptionHandlers() {
		return registeredExceptionHandlers;
	}

	@SuppressWarnings("rawtypes")
	public void setRegisteredExceptionHandlers(Map<String, Class<? extends ExceptionHandler>> registeredExceptionHandlers) {
		this.registeredExceptionHandlers = registeredExceptionHandlers;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Class<? extends DataProvider>> getRegisteredDataProviders() {
		return registeredDataProviders;
	}
	
	@SuppressWarnings("rawtypes")
	public void setRegisteredDataProviders(
			Map<String, Class<? extends DataProvider>> registeredDataProviders) {
		this.registeredDataProviders = registeredDataProviders;
	}
	
	public String getDefaultEncoding() {
		return defaultEncoding;
	}
	
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	@Override
	public Object newInstanceOf(HandledClass routeClass) throws Exception {
		return routeClass.getTargetClass().newInstance();
	}

	@Override
	public ExecutorService getMethodExecutionThreadPool() {
		return methodExecutionThreadPool;
	}

	public void setMethodExecutionThreadPool(ExecutorService executorService) {
		this.methodExecutionThreadPool = executorService;
	}

	@Override
	public ExecutorService getRenderingThreadPool() {
		return renderingThreadPool;
	}
	
	public void setRenderingThreadPool(ExecutorService renderingThreadPool) {
		this.renderingThreadPool = renderingThreadPool;
	}
}

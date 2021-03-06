package layr.routing.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import layr.api.ApplicationContext;
import layr.api.Cache;
import layr.api.ClassFactory;
import layr.api.ComponentFactory;
import layr.api.DataProvider;
import layr.api.ExceptionHandler;
import layr.api.InputConverter;
import layr.api.OutputRenderer;

/**
 * Default implementation of ApplicationContext interface. Developers are
 * encouraged to extends this class in order to avoid rework.
 */
public class DefaultApplicationContextImpl implements ApplicationContext {

	Cache cache;
	String defaultResource;
	String defaultEncoding;
	ExecutorService renderingThreadPool;
	ExecutorService methodExecutionThreadPool;
	List<HandledClass> registeredTemplateParameterObject;

	Map<String, ComponentFactory> registeredTagLibs;
	Map<String, Class<? extends OutputRenderer>> registeredOutputRenderes;
	Map<String, Class<? extends InputConverter>> registeredInputConverter;

	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends ExceptionHandler>> registeredExceptionHandlers;
	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends DataProvider>> registeredDataProviders;
	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends ClassFactory>> registeredClassFactories;

	Map<String, Object> attributes;

	public DefaultApplicationContextImpl() {
		attributes = new HashMap<String, Object>();
	}

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

	public void setRegisteredTagLibs(
			Map<String, ComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setRegisteredWebResources(List<HandledClass> exposedMethods) {
		this.setAttribute(HandledClass.class.getCanonicalName(), exposedMethods);
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Class<? extends ExceptionHandler>> getRegisteredExceptionHandlers() {
		return registeredExceptionHandlers;
	}

	@SuppressWarnings("rawtypes")
	public void setRegisteredExceptionHandlers(
			Map<String, Class<? extends ExceptionHandler>> registeredExceptionHandlers) {
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

	@Override
	public Map<String, Class<? extends OutputRenderer>> getRegisteredOutputRenderers() {
		return registeredOutputRenderes;
	}

	public void setRegisteredOutputRenderes(
			Map<String, Class<? extends OutputRenderer>> registeredOutputRenderes) {
		this.registeredOutputRenderes = registeredOutputRenderes;
	}

	@Override
	public Map<String, Class<? extends InputConverter>> getRegisteredInputConverters() {
		return registeredInputConverter;
	}

	public void setRegisteredInputConverter(
			Map<String, Class<? extends InputConverter>> registeredInputConverter) {
		this.registeredInputConverter = registeredInputConverter;
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Class<? extends ClassFactory>> getRegisteredClassFactories() {
		return registeredClassFactories;
	}

	@SuppressWarnings("rawtypes")
	public void setRegisteredClassFactories(
			Map<String, Class<? extends ClassFactory>> registeredClassFactories) {
		this.registeredClassFactories = registeredClassFactories;
	}
}

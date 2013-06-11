package layr.routing.lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import layr.api.ComponentFactory;
import layr.api.DataProvider;
import layr.api.ExceptionHandler;
import layr.api.TagLib;
import layr.api.WebResource;
import layr.engine.components.DefaultComponentFactory;
import layr.engine.components.template.TemplateComponentFactory;
import layr.engine.components.xhtml.XHtmlComponentFactory;
import layr.exceptions.RoutingInitializationException;
import layr.exceptions.UnhandledException;

public abstract class RoutingBootstrap {

	Map<String, ComponentFactory> registeredTagLibs;
	List<HandledClass> registeredWebResources;

	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends ExceptionHandler>> registeredExceptionHandlers;
	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends DataProvider>> registeredDataProviders;
	
	@SuppressWarnings("rawtypes")
	HandlerClassExtractor<ExceptionHandler> exceptionHandlerClassExtractor;
	@SuppressWarnings("rawtypes")
	HandlerClassExtractor<DataProvider> dataProviderClassExtractor;

	public RoutingBootstrap() {
		registeredWebResources = new ArrayList<HandledClass>();
		registeredTagLibs = new HashMap<String, ComponentFactory>();
		exceptionHandlerClassExtractor = HandlerClassExtractor.newInstance(ExceptionHandler.class);
		dataProviderClassExtractor = HandlerClassExtractor.newInstance(DataProvider.class);

		populateWithDefaultTagLibs( registeredTagLibs );
	}

	/**
	 * @param registeredTagLibs
	 */
	public void populateWithDefaultTagLibs(Map<String, ComponentFactory> registeredTagLibs) {
		XHtmlComponentFactory xHtmlComponentFactory = new XHtmlComponentFactory();
		registeredTagLibs.put("", xHtmlComponentFactory);
		registeredTagLibs.put("http://www.w3.org/1999/xhtml", xHtmlComponentFactory);
		registeredTagLibs.put("urn:layr:template", new TemplateComponentFactory());
	}

	public ApplicationContext configure(Set<Class<?>> classes) throws RoutingInitializationException {
		analyse( classes );
		memorizeRegisteredHandlers();
		return createConfiguration();
	}

	public void memorizeRegisteredHandlers() {
		registeredExceptionHandlers = exceptionHandlerClassExtractor.getRegisteredHandlers();
		if (!registeredExceptionHandlers.containsKey(UnhandledException.class.getCanonicalName()) )
			registeredExceptionHandlers.put(UnhandledException.class.getCanonicalName(), DefaultUnhandledExceptionHandler.class);
		
		registeredDataProviders = dataProviderClassExtractor.getRegisteredHandlers();
	}

	public abstract ApplicationContext createConfiguration() throws RoutingInitializationException;

	public void analyse(Set<Class<?>> classes) throws RoutingInitializationException {
		for (Class<?> clazz : classes)
			try {
				analyse( clazz );
			} catch ( Throwable e ) {
				throw new RoutingInitializationException( e );
			}
	}

	public void analyse(Class<?> clazz) throws Exception {
		tryToRegisterAWebResource(clazz);
		tryToRegisterATag(clazz);
		tryToRegisterAnExceptionHandler(clazz);
		tryToRegisterAnDataProvider(clazz);
	}

	public void tryToRegisterAWebResource(Class<?> clazz) {
		WebResource annotation = clazz.getAnnotation( WebResource.class );
		if (annotation == null)
			return;
		registeredWebResources.add( new HandledClass( clazz ) );
	}

	public void tryToRegisterATag(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		TagLib annotation = clazz.getAnnotation(TagLib.class);
		if (annotation == null)
			return;

		String namespace = annotation.value();
		ComponentFactory factory = (ComponentFactory)clazz.newInstance();

		if (DefaultComponentFactory.class.isInstance(factory))
			((DefaultComponentFactory)factory).setRootDir(
				clazz.getPackage().getName().replace(".", "/"));

		registeredTagLibs.put(namespace, factory);
	}

	public void tryToRegisterAnExceptionHandler(Class<?> clazz){
		exceptionHandlerClassExtractor.extract(clazz);
	}

	public void tryToRegisterAnDataProvider(Class<?> clazz){
		dataProviderClassExtractor.extract(clazz);
	}

	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public List<HandledClass> getRegisteredWebResources() {
		return registeredWebResources;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Class<? extends ExceptionHandler>> getRegisteredExceptionHandlers() {
		return registeredExceptionHandlers;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, Class<? extends DataProvider>> getRegisteredDataProviders() {
		return registeredDataProviders;
	}
}

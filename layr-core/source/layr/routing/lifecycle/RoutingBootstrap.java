package layr.routing.lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import layr.api.ApplicationContext;
import layr.api.ClassFactory;
import layr.api.ComponentFactory;
import layr.api.ContentType;
import layr.api.DataProvider;
import layr.api.ExceptionHandler;
import layr.api.InputConverter;
import layr.api.OutputRenderer;
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
	Map<String, Class<? extends ClassFactory>> registeredClassFactories;
	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends DataProvider>> registeredDataProviders;
	Map<String, Class<? extends OutputRenderer>> registeredOutputRenderers;
	Map<String, Class<? extends InputConverter>> registeredInputConverter;

	@SuppressWarnings("rawtypes")
	HandlerClassExtractor<ExceptionHandler> exceptionHandlerClassExtractor;
	@SuppressWarnings("rawtypes")
	HandlerClassExtractor<DataProvider> dataProviderClassExtractor;
	@SuppressWarnings("rawtypes")
	HandlerClassExtractor<ClassFactory> classFactoriesClassExtractor;

	public RoutingBootstrap() {
		registeredWebResources = new ArrayList<HandledClass>();
		registeredTagLibs = new HashMap<String, ComponentFactory>();
		registeredOutputRenderers = new HashMap<String, Class<? extends OutputRenderer>>();
		registeredInputConverter = new HashMap<String, Class<? extends InputConverter>>();
		classFactoriesClassExtractor = HandlerClassExtractor.newInstance(ClassFactory.class);
		exceptionHandlerClassExtractor = HandlerClassExtractor.newInstance(ExceptionHandler.class);
		dataProviderClassExtractor = HandlerClassExtractor.newInstance(DataProvider.class);
		populateWithDefaultTagLibs( registeredTagLibs );
		populateWithDefaultOutputRenderers( registeredOutputRenderers );
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

	public void populateWithDefaultOutputRenderers(
			Map<String, Class<? extends OutputRenderer>> outputRenderers) {
		outputRenderers.put("text/html", XHTMLOutputRenderer.class);
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
		registeredClassFactories = classFactoriesClassExtractor.getRegisteredHandlers();
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
		tryToRegisterAContentTypeRenderer(clazz);
		tryToRegisterAContentTypeInputConverter(clazz);
		tryToRegisterAClassFactory(clazz);
	}

	public void tryToRegisterAClassFactory(Class<?> clazz) {
		classFactoriesClassExtractor.extract(clazz);
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

	@SuppressWarnings("unchecked")
	public void tryToRegisterAContentTypeRenderer(Class<?> clazz) {
		ContentType contentTypeAnn = clazz.getAnnotation(ContentType.class);
		if ( contentTypeAnn != null )
			for ( String contentType : contentTypeAnn.value() )
				registeredOutputRenderers.put(contentType, (Class<? extends OutputRenderer>) clazz);
	}

	@SuppressWarnings("unchecked")
	public void tryToRegisterAContentTypeInputConverter(Class<?> clazz) {
		ContentType contentTypeAnn = clazz.getAnnotation(ContentType.class);
		if ( contentTypeAnn != null )
			for ( String contentType : contentTypeAnn.value() )
				registeredInputConverter.put(contentType, (Class<? extends InputConverter>) clazz);
	}

	public Map<String, Class<? extends OutputRenderer>> getRegisteredOutputRenderers() {
		return registeredOutputRenderers;
	}
	
	public Map<String, Class<? extends InputConverter>> getRegisteredInputConverter() {
		return registeredInputConverter;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Class<? extends ClassFactory>> getRegisteredClassFactories() {
		return registeredClassFactories;
	}
}

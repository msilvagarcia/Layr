package layr.routing.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import layr.engine.components.ComponentFactory;
import layr.engine.components.DefaultComponentFactory;
import layr.engine.components.TagLib;
import layr.engine.components.template.TemplateComponentFactory;
import layr.engine.components.xhtml.XHtmlComponentFactory;
import layr.routing.annotations.Handler;
import layr.routing.annotations.WebResource;
import layr.routing.api.Configuration;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.RouteClass;
import layr.routing.exceptions.RoutingInitializationException;

public abstract class RoutingBootstrap {

	protected Map<String, ComponentFactory> registeredTagLibs;
	protected List<RouteClass> registeredWebResources;
	protected Map<String, Class<ExceptionHandler<?>>> registeredExceptionHandlers;

	public RoutingBootstrap() {
		registeredWebResources = new ArrayList<RouteClass>();
		registeredTagLibs = new HashMap<String, ComponentFactory>();
		registeredExceptionHandlers = new HashMap<String, Class<ExceptionHandler<?>>>();
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

	public Configuration configure(Set<Class<?>> classes) throws RoutingInitializationException {
		analyse( classes );
		return createConfiguration();
	}

	public abstract Configuration createConfiguration();

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
	}

	public void tryToRegisterAWebResource(Class<?> clazz) {
		WebResource annotation = clazz.getAnnotation( WebResource.class );
		if (annotation == null)
			return;
		registeredWebResources.add( new RouteClass( clazz ) );
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

	public void tryToRegisterAnExceptionHandler(Class<?> clazz) {
		if ( !clazz.isAnnotationPresent( Handler.class )
		||   !ExceptionHandler.class.isAssignableFrom( clazz ))
			return;

		for ( Type type : clazz.getGenericInterfaces() )
			registerExceptionHandler( clazz, type );
	}

	@SuppressWarnings("unchecked")
	public void registerExceptionHandler(Class<?> clazz, Type type) {
		if ( !ParameterizedType.class.isInstance( type ) )
			return;
		
		ParameterizedType ptype = (ParameterizedType)type;
		if ( ExceptionHandler.class.equals( ptype.getRawType() ) ){
			Class<?> exceptionClass = (Class<?>)ptype.getActualTypeArguments()[0];
			registeredExceptionHandlers.put( exceptionClass.getCanonicalName(), (Class<ExceptionHandler<?>>) clazz );
		}
	}

	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public List<RouteClass> getRegisteredWebResources() {
		return registeredWebResources;
	}

	public Map<String, Class<ExceptionHandler<?>>> getExceptionHandlers() {
		return registeredExceptionHandlers;
	}

}

package layr.routing;

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
import layr.routing.annotations.WebResource;

public class RoutingBootstrap {

	Map<String, ComponentFactory> registeredTagLibs;
	List<RouteClass> registeredWebResources;

	public RoutingBootstrap() {
		registeredWebResources = new ArrayList<RouteClass>();
		registeredTagLibs = new HashMap<String, ComponentFactory>();
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

	public Configuration configure( Set<Class<?>> classes ) throws RoutingInitializationException{
		analyse( classes );
		return createConfiguration();
	}
	
	public void analyse( Set<Class<?>> classes ) throws RoutingInitializationException{
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
	}

	public void tryToRegisterAWebResource(Class<?> clazz) {
		WebResource annotation = clazz.getAnnotation( WebResource.class );
		if (annotation == null)
			return;

		registeredWebResources.add( new RouteClass( clazz ) );
	}

	public void tryToRegisterATag(Class<?> clazz)
			throws InstantiationException, IllegalAccessException {
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
	
	public Configuration createConfiguration(){
		StubConfiguration configuration = new StubConfiguration();
		configuration.setRegisteredTagLibs( registeredTagLibs );
		configuration.setRegisteredWebResources( registeredWebResources );
		return configuration;
	}

	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}
	
	public List<RouteClass> getRegisteredWebResources() {
		return registeredWebResources;
	}
}

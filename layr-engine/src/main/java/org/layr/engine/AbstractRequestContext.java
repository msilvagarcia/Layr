package org.layr.engine;

import static org.layr.commons.StringUtil.stripURLFirstSlash;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.layr.commons.Cache;
import org.layr.engine.components.IComponent;
import org.layr.engine.components.IComponentFactory;
import org.layr.engine.components.template.TemplateComponentFactory;
import org.layr.engine.components.xhtml.XHtmlComponentFactory;

public abstract class AbstractRequestContext implements IRequestContext {

	private final static String COMPONENTS = "AbstractRequestContext.COMPONENTS";

	private Map<String, Object> properties;
	private Set<String> availableLocalResourceFiles;
	private Map<String, IComponentFactory> registeredTagLibs;
	private int idComponentCounter;
	private String webResourceRootPath;
	private String applicationRootPath;
	private String relativePath;
	private Cache cache;

	public AbstractRequestContext() {
		properties = new HashMap<String, Object>();
	}

	@Override
	public void registerNamespace(String namespace, IComponentFactory factory) {
		getRegisteredTagLibs().put(namespace, factory);
	}

	@Override
	public IComponentFactory getComponentFactory(String namespace) {
		return getRegisteredTagLibs().get(namespace);
	}

	@Override
	public boolean isRegisteredNamespace(String namespace) {
		return getRegisteredTagLibs().containsKey(namespace);
	}

	@Override
	public IRequestContext put(String name, Object property) {
		if ( property == null )
			properties.remove(name);
		else
			properties.put(name, property);
		return this;
	}

	@Override
	public Object get(String name) {
		return properties.get(name);
	}

	public void setIdComponentCounter(int idComponentCounter) {
		this.idComponentCounter = idComponentCounter;
	}

	public int getIdComponentCounter() {
		return idComponentCounter;
	}

	@Override
	public String getNextId() {
		return "component" + (idComponentCounter++);
	}

	@Override
	public String getWebResourceRootPath() {
		return webResourceRootPath;
	}

	@Override
	public void setWebResourceRootPath(String path) {
		if (!path.endsWith("/"))
			path = path + "/";
		this.webResourceRootPath = path;
	}

	@Override
	public void log(String text) {
		System.out.println(text);
	}

	/**
	 * Retrieves dynamically a resource as Stream from a resourceName.
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public InputStream getResourceAsStream(String resourceName)
			throws IOException {
		resourceName = stripURLFirstSlash(resourceName);
		InputStream stream = openStream(resourceName);
		return stream;
	}

	/**
	 * @param url
	 * @return
	 */
	public InputStream openStream(String url) {
		return getClassLoader().getResourceAsStream(url);
	}

	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public Set<String> getAvailableLocalResourceFiles() {
		return availableLocalResourceFiles;
	}

	public void setAvailableLocalResourceFiles(
			Set<String> availableLocalResourceFiles) {
		this.availableLocalResourceFiles = availableLocalResourceFiles;
	}

	/**
	 * @param templateName
	 * @return
	 */
	public IComponent getResourceFromCache(String templateName) {
		Map<String, IComponent> cachedComponents = getCachedComponents();
		if ( cachedComponents == null )
			return null;
		return cachedComponents.get(templateName);
	}

	/**
	 * @param templateName
	 * @param application
	 */
	public void putInCacheTheCompiledResource(String templateName, IComponent application) {
		if ( application == null
		||   templateName == null
		||   templateName.isEmpty())
			return;

		Map<String, IComponent> compiledComponents = getCachedComponents();
		if ( compiledComponents != null )
			compiledComponents.put(templateName, application);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, IComponent> getCachedComponents() {
		if ( cache == null )
			return null;

		Map<String, IComponent> compiledComponents = (Map<String,IComponent>)cache.get( COMPONENTS );
		if ( compiledComponents == null ){
			compiledComponents = new HashMap<String, IComponent>();
			cache.put(COMPONENTS, compiledComponents);
		}
		return compiledComponents;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public String getApplicationRootPath() {
		return applicationRootPath;
	}
	
	@Override
	public void setApplicationRootPath(String contextPath) {
		this.applicationRootPath = contextPath;
	}

	@Override
	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public Map<String, IComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public void setRegisteredTagLibs(Map<String, IComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	public static void populateWithDefaultTagLibs(Map<String, IComponentFactory> registeredTagLibs) {
		XHtmlComponentFactory xHtmlComponentFactory = new XHtmlComponentFactory();
		registeredTagLibs.put("", xHtmlComponentFactory);
		registeredTagLibs.put("http://www.w3.org/1999/xhtml", xHtmlComponentFactory);
		registeredTagLibs.put("urn:layr:template", new TemplateComponentFactory());
	}
}

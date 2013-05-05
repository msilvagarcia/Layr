package layr.engine;

import static layr.commons.StringUtil.stripURLFirstSlash;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import layr.engine.components.Component;
import layr.engine.components.ComponentFactory;

public abstract class AbstractRequestContext implements RequestContext {

	Map<String, Object> properties;
	Map<String, ComponentFactory> registeredTagLibs;
	Cache cache;

	public AbstractRequestContext() {
		properties = new HashMap<String, Object>();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public void put(String name, Object property) {
		if ( property == null )
			properties.remove(name);
		else
			properties.put(name, property);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#get(java.lang.String)
	 */
	@Override
	public Object get(String name) {
		return properties.get(name);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#registerNamespace(java.lang.String, org.layr.engine.components.ComponentFactory)
	 */
	@Override
	public void registerNamespace(String namespace, ComponentFactory factory) {
		getRegisteredTagLibs().put(namespace, factory);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#getComponentFactory(java.lang.String)
	 */
	@Override
	public ComponentFactory getComponentFactory(String namespace) {
		return getRegisteredTagLibs().get(namespace);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#isRegisteredNamespace(java.lang.String)
	 */
	@Override
	public boolean isRegisteredNamespace(String namespace) {
		return getRegisteredTagLibs().containsKey(namespace);
	}

	/**
	 * @return
	 */
	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	/**
	 * @param registeredTagLibs
	 */
	public void setRegisteredTagLibs(Map<String, ComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#log(java.lang.String)
	 */
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

	/**
	 * @return
	 */
	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * @param templateName
	 * @return
	 */
	public Component getResourceFromCache(String templateName) {
		Map<String, Component> cachedComponents = getCachedSnippets();
		if ( cachedComponents == null )
			return null;
		return cachedComponents.get(templateName);
	}

	/**
	 * @param templateName
	 * @param application
	 */
	public void cacheCompiledResource(String templateName, Component application) {
		if ( application == null
		||   templateName == null
		||   templateName.isEmpty())
			return;

		Map<String, Component> compiledSnippets = getCachedSnippets();
		if ( compiledSnippets != null )
			compiledSnippets.put(templateName, application);
	}

	/**
	 * @return
	 */
	public Map<String, Component> getCachedSnippets() {
		if ( cache == null )
			return null;
		return cache.getCompiledSnippets();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.RequestContext#getCache()
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * @param cache
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}
}

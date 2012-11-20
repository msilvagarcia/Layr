package org.layr.engine;

import static org.layr.commons.StringUtil.stripURLFirstSlash;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.layr.commons.Cache;
import org.layr.engine.components.IComponent;
import org.layr.engine.components.IComponentFactory;
import org.layr.engine.components.template.TemplateComponentFactory;
import org.layr.engine.components.xhtml.XHtmlComponentFactory;
import org.xml.sax.SAXException;

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
		setRegisteredTagLibs(new HashMap<String, IComponentFactory>());
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

	/**
	 * Get a resource and read it as a StringBufffer.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public StringBuilder getResourceAsStringBuilder(String fileName) throws IOException {
		InputStream stream = getResourceAsStream(fileName);
		StringBuilder buffer = new StringBuilder();
		
		if (stream == null)
			throw new FileNotFoundException("Can't read filename '" + fileName + "'");

		byte[] b = new byte[4096];
		for (int n; (n = stream.read(b)) != -1;) {
			buffer.append(new String(b, 0, n));
		}

		return buffer;
	}

	public Set<String> getAvailableLocalResourceFiles() {
		return availableLocalResourceFiles;
	}

	public void setAvailableLocalResourceFiles(
			Set<String> availableLocalResourceFiles) {
		this.availableLocalResourceFiles = availableLocalResourceFiles;
	}

	/**
	 * Compiles the resource. For performance improvements it caches
	 * compiled templates for further usage.
	 * 
	 * @param teplateName
	 * @param layrContext
	 * @return IComponent implementation of compiled template
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws CloneNotSupportedException
	 * @throws ServletException 
	 */
	public IComponent compile(String templateName)
			throws IOException, ParserConfigurationException, SAXException,
				CloneNotSupportedException, ServletException {
		IComponent component = getResourceFromCache( templateName );
		if (component != null)
			return (IComponent) component.clone(this);

		InputStream template = this.getResourceAsStream(templateName);
		if (template == null)
			return null;

		try{
			IComponent application = compileTemplate(template);
	        cacheCompiledPage(templateName, application);
			return application;
		} catch (Throwable e){
			throw new ServletException("Can't parse '" + templateName + "' as XHTML.", e);
		}
	}

	/**
	 * @param context
	 * @param template
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public IComponent compileTemplate(InputStream template)
			throws ParserConfigurationException, SAXException, IOException {
		TemplateParser parser = new TemplateParser(this);
		IComponent application = parser.parse(template);
	    application.setDocTypeDefinition(parser.getDoctype());
		return application;
	}

	/**
	 * @param templateName
	 * @param application
	 */
	public void cacheCompiledPage(String templateName, IComponent application) {
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
		populateWithDefaultTagLibs(registeredTagLibs);
		this.registeredTagLibs = registeredTagLibs;
	}

	public void populateWithDefaultTagLibs(Map<String, IComponentFactory> registeredTagLibs) {
		XHtmlComponentFactory xHtmlComponentFactory = new XHtmlComponentFactory();
		registeredTagLibs.put("", xHtmlComponentFactory);
		registeredTagLibs.put("http://www.w3.org/1999/xhtml", xHtmlComponentFactory);
		registeredTagLibs.put("urn:layr:template", new TemplateComponentFactory());
	}
}

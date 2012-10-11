package layr.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.xml.parsers.ParserConfigurationException;

import layr.ApplicationContext;
import layr.Configuration;
import layr.LayrFactory;
import layr.annotation.TagLib;
import layr.annotation.WebResource;
import layr.commons.EnterpriseJavaBeans;
import layr.commons.StringUtil;
import layr.components.DefaultComponentFactory;
import layr.components.IComponentFactory;
import layr.resources.AbstractSystemResourceLoader;
import layr.resources.ClassPathResourceLoader;
import layr.resources.ServletContextResourceLoader;

import org.xml.sax.SAXException;


@HandlesTypes({
	Stateless.class,
	Stateful.class,
	Singleton.class,
	TagLib.class,
	WebResource.class
})
public class LayrInitializer implements ServletContainerInitializer {

	private static final String CONFIGURATION_FILE = "META-INF/layr.xml";
	private Map<String, IComponentFactory> registeredTagLibs;
	private Map<String, Object> registeredWebResources;
	private EnterpriseJavaBeans ejbManager;
	private ApplicationContext applicationContext;
	
	/**
	 * @param servletContext
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ServletException
	 */
	public void manuallyInitialize(ServletContext servletContext) throws IOException, ClassNotFoundException, ServletException {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		AbstractSystemResourceLoader[] applicationContexts = new AbstractSystemResourceLoader[] {
			new ServletContextResourceLoader(servletContext, classLoader),
			new ClassPathResourceLoader(classLoader)
		};
		
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		for (AbstractSystemResourceLoader applicationContext : applicationContexts) {
			for (Class<?> clazz : applicationContext.retrieveAvailableClasses()) {
				classes.add(clazz);
			}
		}
		
		onStartup(classes, servletContext);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContainerInitializer#onStartup(java.util.Set, javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
			throws ServletException {

		try {
			initializeApplicationContext(servletContext);
			readConfiguration();
		
			for (Class<?> clazz : classes) {
				tryToRegisterATag(clazz);
				tryToRegisterAWebResource(clazz);
				ejbManager.seekForJNDIEJBViewsfor(clazz);
			}

		} catch (Throwable e) {
			throw new ServletException("Can't initialize the Application Context.", e);
		}

		servletContext.log("[Layr] Configuration Loaded!");
		servletContext.log("[Layr] Is Request Cache Enabled: " + applicationContext.getLayrConfiguration().isCacheEnabled());
		servletContext.log("[Layr] Is Equation Support Enabled (it reduce 25% performance): " + (!applicationContext.getLayrConfiguration().isEquationsDisabled()));
		servletContext.log("[Layr] Default Welcome Resource mapped to: " + applicationContext.getLayrConfiguration().getDefaultResource());
		servletContext.log("[Layr] Application Context initialized.");
	}

	/**
	 * Initialize Layr Application Context and configure it for further use.
	 * @param servletContext
	 */
	public void initializeApplicationContext(ServletContext servletContext) throws NamingException {
		applicationContext = LayrFactory.getOrCreateApplicationContext(servletContext);
		applicationContext.initializeApplicationContexts();
		
		ejbManager = new EnterpriseJavaBeans();
		registeredTagLibs = new HashMap<String, IComponentFactory>();
		registeredWebResources = new HashMap<String, Object>();

		applicationContext.setEjbManager(ejbManager);
		applicationContext.setRegisteredTagLibs(registeredTagLibs);
		applicationContext.setRegisteredWebResources(registeredWebResources);
	}

	/**
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void readConfiguration() throws IOException, ParserConfigurationException, SAXException {
		InputStream stream = readConfigurationFile();
		if ( stream == null ){
			applicationContext.getServletContext().log("[Layr] No specific configurations found.");
			applicationContext.setLayrConfiguration(new Configuration());
			return;
		}

		Configuration configuration = Configuration.parse(stream);
		applicationContext.setLayrConfiguration(configuration);
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	public InputStream readConfigurationFile() throws IOException {
		InputStream stream = applicationContext.getResourceAsStream(CONFIGURATION_FILE);

		if ( stream == null )
			stream = applicationContext.getResourceAsStream("WEB-INF/classes/" + CONFIGURATION_FILE);
		
		return stream;
	}

	/**
	 * Try to Register a WebResource based on class annotation.
	 * @param clazz
	 */
	public void tryToRegisterAWebResource(Class<?> clazz) {
		WebResource annotation = clazz.getAnnotation(WebResource.class);
		if (annotation == null)
			return;

		String url = annotation.value();
		if (StringUtil.isEmpty(url))
			url = annotation.rootURL();

		if (StringUtil.isEmpty(url))
			return;
		
		getRegisteredWebResources().put(url, clazz);
	}

	/**
	 * Try to Register a TagLib based on class annotation.
	 * 
	 * @param clazz
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void tryToRegisterATag(Class<?> clazz)
			throws InstantiationException, IllegalAccessException
	{
		TagLib annotation = clazz.getAnnotation(TagLib.class);
		if (annotation == null)
			return;

		String namespace = annotation.value();
		IComponentFactory factory = (IComponentFactory)clazz.newInstance();

		if (DefaultComponentFactory.class.isInstance(factory))
			((DefaultComponentFactory)factory).setRootDir(
				clazz.getPackage().getName().replace(".", "/"));

		getRegisteredTagLibs().put(namespace, factory);
	}

	public Map<String, IComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public void setRegisteredTagLibs(Map<String, IComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	public Map<String, Object> getRegisteredWebResources() {
		return registeredWebResources;
	}

	public void setRegisteredWebResources(Map<String, Object> registeredWebResources) {
		this.registeredWebResources = registeredWebResources;
	}

	public EnterpriseJavaBeans getEjbManager() {
		return ejbManager;
	}

	public void setEjbManager(EnterpriseJavaBeans ejbManager) {
		this.ejbManager = ejbManager;
	}

}

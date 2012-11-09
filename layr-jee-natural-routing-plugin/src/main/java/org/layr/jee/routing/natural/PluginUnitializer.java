package org.layr.jee.routing.natural;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.layr.jee.commons.JEEConfiguration;

@WebListener
public class PluginUnitializer implements ServletContextListener {
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		servletContext.log("[Layr] Cleaning Some Caches...");

		JEEConfiguration configuration = (JEEConfiguration)servletContext.getAttribute(
					JEEConfiguration.class.getCanonicalName() );
		if ( configuration != null && configuration.getCache() != null )
			configuration.getCache().clearCache();

		System.gc();
		servletContext.log("[Layr] Done! Released memory will be available next time Garbage Collection back to the scene.");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {}

}

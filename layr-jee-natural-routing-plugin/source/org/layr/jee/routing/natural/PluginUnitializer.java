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
		JEEConfiguration configuration = (JEEConfiguration)servletContext.getAttribute(
					JEEConfiguration.class.getCanonicalName() );
		if ( configuration != null && configuration.getCache() != null )
			configuration.getCache().clearCache();

		System.gc();
		servletContext.log("[Layr] JEE Natural Routing Plugin finished.");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {}

}

package layr.routing.jee;

import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import layr.engine.components.TagLib;
import layr.routing.api.ApplicationContext;
import layr.routing.api.Handler;
import layr.routing.api.WebResource;

@HandlesTypes({
	TagLib.class,
	WebResource.class,
	Stateless.class,
	Stateful.class,
	Singleton.class,
	Handler.class
})
public class Initialization implements javax.servlet.ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
		try {
			JEERoutingBootstrap bootstrap = new JEERoutingBootstrap();
			ApplicationContext applicationContext = bootstrap.configure(classes);
			servletContext.setAttribute(JEEConfiguration.class.getCanonicalName(), applicationContext);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}

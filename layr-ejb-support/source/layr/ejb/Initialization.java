package layr.ejb;

import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes({
	Stateless.class,
	Stateful.class,
	Singleton.class
})
public class Initialization implements javax.servlet.ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
		try {
			EnterpriseJavaBeansContext context = analyse( classes );
			servletContext.setAttribute(EnterpriseJavaBeansContext.class.getCanonicalName(), context);
			servletContext.log("Layr EJB support configured.");
		} catch (Exception e) {
			servletContext.log("Layr EJB support failed to start.");
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	public EnterpriseJavaBeansContext analyse(Set<Class<?>> classes) throws NamingException {
		EnterpriseJavaBeansContext context = new EnterpriseJavaBeansContext();
		for ( Class<?> clazz : classes )
			context.seekForJNDIEJBViewsfor(clazz);
		return context;
	}
}

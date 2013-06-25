package layr.jee;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import layr.commons.StringUtil;

public class EnterpriseJavaBeansContext {
	
	private InitialContext context;
	private Map<String, String> registeredEJBViews;
	
	public EnterpriseJavaBeansContext() throws NamingException {
		//context = new InitialContext();
	}

	/**
	 * Mapping EJB Global JNDI as defined at Oracle's EJB 3.1 Documentation
	 * http://docs.oracle.com/javaee/6/tutorial/doc/gipjf.html#girgn
	 * 
	 * @param clazz
	 */
	public void seekForJNDIEJBViewsfor(Class<?> clazz) {
		Stateless stateless = clazz.getAnnotation(Stateless.class);
		Stateful stateful = clazz.getAnnotation(Stateful.class);
		Singleton singleton = clazz.getAnnotation(Singleton.class);

		if (stateless == null && stateful == null && singleton == null)
			return ;

		registerAnnotatedEJBViewsInterfaces(clazz);
		registerEJBViewsFromImplementedInterface(clazz);
		register(clazz);
	}

	/**
	 * @param clazz
	 */
	public void registerEJBViewsFromImplementedInterface(Class<?> clazz) {
		Class<?>[] interfaces = clazz.getInterfaces();

		for (Class<?> interfaceClass : interfaces)
			register(clazz, interfaceClass);
	}

	/**
	 * @param clazz
	 */
	public void registerAnnotatedEJBViewsInterfaces(Class<?> clazz) {
		Local local = clazz.getAnnotation(Local.class);
		Remote remote = clazz.getAnnotation(Remote.class);

		if (local != null)
			for (Class<?> interfaceClass : local.value())
				register(clazz, interfaceClass);

		if (remote != null)
			for (Class<?> interfaceClass : remote.value())
				register(clazz, interfaceClass);
	}

	/**
	 * @param clazz
	 * @param interfaceClass
	 */
	public void register(Class<?> clazz, Class<?> interfaceClass) {
		register(
			interfaceClass.getCanonicalName(),
			clazz.getSimpleName() + "!" + interfaceClass.getCanonicalName());
	}

	/**
	 * @param clazz
	 */
	public void register(Class<?> clazz) {
		register(clazz.getCanonicalName(),
				clazz.getSimpleName() + "!" + clazz.getCanonicalName());
	}
	
	public void register(String clazz, String jndiPath) {
		getRegisteredEJBViews().put(clazz, jndiPath);
	}
	
	public Object lookup( Class<?> clazz ) throws NamingException {
		if ( clazz.isAnnotationPresent( Stateless.class ) )
			return lookupJNDI( clazz, clazz.getAnnotation( Stateless.class ).mappedName() );
		if ( clazz.isAnnotationPresent( Stateful.class ) )
			return lookupJNDI( clazz, clazz.getAnnotation( Stateful.class ).mappedName() );
		if ( clazz.isAnnotationPresent( Singleton.class ) )
			return lookupJNDI( clazz, clazz.getAnnotation( Singleton.class ).mappedName() );
		return null;
	}

	public Object lookupJNDI(Class<?> clazz, String jndiName)
			throws NamingException {
		if ( StringUtil.isEmpty(jndiName) )
				jndiName = "java:module/" + getRegisteredEJBViews().get(clazz.getCanonicalName());
		return lookup(jndiName);
	}

	public Object lookup(String jndiName) throws NamingException {
		return getContext().lookup(jndiName);
	}

	public Map<String, String> getRegisteredEJBViews() {
		if (registeredEJBViews == null)
			registeredEJBViews = new HashMap<String, String>();
		return registeredEJBViews;
	}

	public void setRegisteredEJBViews(Map<String, String> registeredEJBViews) {
		this.registeredEJBViews = registeredEJBViews;
	}

	public InitialContext getContext() throws NamingException {
		if ( context == null )
			context = new InitialContext();
		return context;
	}

	public void setContext(InitialContext context) {
		this.context = context;
	}
}

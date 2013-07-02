package layr.routing.impl;

import java.lang.reflect.InvocationTargetException;

import layr.api.ApplicationContext;
import layr.api.ClassFactory;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.commons.Reflection;
import layr.exceptions.ClassFactoryException;

@Handler
public class BlahAnnotationClassFactory implements ClassFactory<Blah> {

	@Override
	public Object newInstance(ApplicationContext applicationContext,
			RequestContext requestContext, Class<?> clazz)
			throws ClassFactoryException {
		try{
			Object newInstance = clazz.newInstance();
			setAttribute(newInstance);
			return newInstance;
		} catch ( Throwable cause ) {
			throw new ClassFactoryException("Fail!", cause);
		}
	}

	private void setAttribute(Object newInstance) throws IllegalAccessException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException {
		try {
			Reflection.setAttribute(newInstance, "injected", "Injected");
		} catch ( Throwable e ) {}
	}
}

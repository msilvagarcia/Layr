package layr.routing.lifecycle;

import java.lang.annotation.Annotation;
import java.util.Map;

import layr.api.ApplicationContext;
import layr.api.ClassFactory;
import layr.api.RequestContext;
import layr.exceptions.ClassFactoryException;

public class ClassInstantiationFactory {

	ApplicationContext configuration;
	RequestContext requestContext;

	public ClassInstantiationFactory(
			ApplicationContext configuration,
			RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
	}

	public Object newInstanceOf(Class<?> targetClass)
			throws ClassFactoryException {
		try {
			ClassFactory<?> classFactory = getClassFactory(targetClass);
			return classFactory.newInstance(configuration, requestContext, targetClass);
		} catch (InstantiationException e) {
			throw new ClassFactoryException("Bad ClassFactory instantiation.", e);
		} catch (IllegalAccessException e) {
			throw new ClassFactoryException("Bad ClassFactory instantiation.", e);
		}
	}

	@SuppressWarnings("rawtypes")
	private ClassFactory<?> getClassFactory(Class<?> targetClass) throws InstantiationException, IllegalAccessException {
		Map<String, Class<? extends ClassFactory>> registeredClassFactories = configuration.getRegisteredClassFactories();
		Class<? extends ClassFactory> classFactory = null;
		for (Annotation annotation : targetClass.getAnnotations()) {
			classFactory = registeredClassFactories.get(annotation.annotationType().getCanonicalName());
			if ( classFactory != null )
				return classFactory.newInstance();
		}
		return new DefaultClassFactory();
	}
}

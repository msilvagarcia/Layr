package layr.api;

import layr.exceptions.ClassFactoryException;

public interface ClassFactory<T> {

	Object newInstance(ApplicationContext applicationContext,
			RequestContext requestContext, Class<?> clazz) throws ClassFactoryException;

}

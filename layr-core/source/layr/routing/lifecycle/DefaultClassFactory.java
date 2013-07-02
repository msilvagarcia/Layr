package layr.routing.lifecycle;

import layr.api.ApplicationContext;
import layr.api.ClassFactory;
import layr.api.RequestContext;
import layr.exceptions.ClassFactoryException;

public class DefaultClassFactory implements ClassFactory<DefaultClassFactory> {

	@Override
	public Object newInstance(ApplicationContext applicationContext,
			RequestContext requestContext, Class<?> clazz) throws ClassFactoryException {
		try {
			return clazz.newInstance();
		} catch (Throwable e) {
			throw new ClassFactoryException("Can't instantiate " + clazz.getCanonicalName(), e);
		}
	}
}

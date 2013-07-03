package layr.ejb;

import javax.ejb.Singleton;

import layr.api.ClassFactory;
import layr.api.Handler;

@Handler
public class SingletonClassFactory
	extends EJBClassFactory implements ClassFactory<Singleton> {}

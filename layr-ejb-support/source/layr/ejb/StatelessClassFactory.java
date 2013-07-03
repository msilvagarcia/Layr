package layr.ejb;

import javax.ejb.Stateless;

import layr.api.ClassFactory;
import layr.api.Handler;

@Handler
public class StatelessClassFactory
	extends EJBClassFactory implements ClassFactory<Stateless> {}

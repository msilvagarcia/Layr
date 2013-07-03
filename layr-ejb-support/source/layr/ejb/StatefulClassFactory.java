package layr.ejb;

import javax.ejb.Stateful;

import layr.api.ClassFactory;
import layr.api.Handler;

@Handler
public class StatefulClassFactory
	extends EJBClassFactory implements ClassFactory<Stateful> {}

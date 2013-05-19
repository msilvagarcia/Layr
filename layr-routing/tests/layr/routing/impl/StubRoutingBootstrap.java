package layr.routing.impl;

import layr.engine.Cache;
import layr.routing.api.ApplicationContext;
import layr.routing.lifecycle.RoutingBootstrap;

public class StubRoutingBootstrap extends RoutingBootstrap {

	public ApplicationContext createConfiguration(){
		StubConfiguration configuration = new StubConfiguration();
		configuration.setRegisteredTagLibs( registeredTagLibs );
		configuration.setRegisteredWebResources( registeredWebResources );
		configuration.setRegisteredDataProviders(getRegisteredDataProviders());
		configuration.setRegisteredExceptionHandlers( registeredExceptionHandlers );
		configuration.setCache( new Cache() );
		return configuration;
	}

}

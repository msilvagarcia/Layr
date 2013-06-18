package layr.routing.impl;

import layr.api.Cache;
import layr.routing.lifecycle.ApplicationContext;
import layr.routing.lifecycle.RoutingBootstrap;

public class StubRoutingBootstrap extends RoutingBootstrap {

	public ApplicationContext createConfiguration(){
		StubApplicationContext configuration = new StubApplicationContext();
		configuration.setDefaultEncoding( "UTF-8" );
		configuration.setRegisteredTagLibs( getRegisteredTagLibs() );
		configuration.setRegisteredWebResources( getRegisteredWebResources() );
		configuration.setRegisteredDataProviders(getRegisteredDataProviders());
		configuration.setRegisteredExceptionHandlers( getRegisteredExceptionHandlers() );
		configuration.setRegisteredOutputRenderes(getRegisteredOutputRenderers());
		configuration.setCache( new Cache() );
		return configuration;
	}

}

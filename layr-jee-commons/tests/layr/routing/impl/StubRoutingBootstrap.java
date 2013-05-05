package layr.routing.impl;

import layr.routing.Configuration;
import layr.routing.RoutingBootstrap;


public class StubRoutingBootstrap extends RoutingBootstrap {

	public Configuration createConfiguration(){
		StubConfiguration configuration = new StubConfiguration();
		configuration.setRegisteredTagLibs( registeredTagLibs );
		configuration.setRegisteredWebResources( registeredWebResources );
		return configuration;
	}
}

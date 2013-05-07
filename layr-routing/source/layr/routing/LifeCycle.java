package layr.routing;

import layr.engine.RequestContext;

/**
 * Internal interface that defines the basic life cycle circle to render
 * something with Layr.
 */
public interface LifeCycle {

	public void run() throws Exception;

	void createContext(ContainerRequestData<?, ?> containerRequestData);
	
	public RequestContext getRequestContext();

}

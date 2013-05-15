package layr.routing.service;

import layr.engine.RequestContext;

/**
 * Internal interface that defines the basic life cycle circle to render
 * something with Layr.
 */
public interface LifeCycle {

	public void run() throws Exception;

	public RequestContext getRequestContext();

}

package layr.routing;

import layr.engine.RequestContext;

public interface LifeCycle {

	public void run() throws Exception;

	public RequestContext getRequestContext();

}

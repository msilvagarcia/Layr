package layr.routing.lifecycle;

import layr.api.RequestContext;
import layr.commons.Listener;

/**
 * Internal interface that defines the basic life cycle circle to render
 * something with Layr.
 */
public interface LifeCycle {
	
	public boolean canHandleRequest() throws Exception;

	public void run();

	public void onSuccess(Listener<Object> listener);

	public void onFail(Listener<Exception> listener);

	public RequestContext getRequestContext();

}

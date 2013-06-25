package layr.routing.lifecycle;

import static layr.commons.ListenableCall.listenable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import layr.api.RequestContext;
import layr.commons.ListenableCall;
import layr.commons.Listener;

public class BusinessRoutingLifeCycle implements LifeCycle {

    ApplicationContext configuration;
	RequestContext requestContext;
	Listener<Object> onSuccess;
	List<Listener<Exception>> onFail;
	HandledMethod matchedRouteMethod;

	public BusinessRoutingLifeCycle(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
    	this.onFail = new ArrayList<Listener<Exception>>();
	}
	
	@Override
	public boolean canHandleRequest() {
		matchedRouteMethod = getMatchedRouteMethod();
		return matchedRouteMethod != null;
	}

	public HandledMethod getMatchedRouteMethod() {
		BusinessRoutingMethodMatching businessRoutingMethodMatching = new BusinessRoutingMethodMatching( configuration, requestContext );
		HandledMethod routeMethod = businessRoutingMethodMatching.getMatchedRouteMethod();
		return routeMethod;
	}

    public void run() {
		runMethod( matchedRouteMethod );
    }

	public void runMethod(HandledMethod routeMethod) {
		try {
			ListenableCall listenable = createListenableAsyncRunner(routeMethod);
			Future<?> submit = configuration.getMethodExecutionThreadPool().submit(listenable);
			if ( !requestContext.isAsyncRequest() )
				submit.get();
		} catch ( ExecutionException e ) {
			onFail(e);
		} catch (Exception e) {
			onFail(e);
		}
	}

	public ListenableCall createListenableAsyncRunner(
			HandledMethod routeMethod) throws Exception {
		Object instance = configuration.newInstanceOf(routeMethod.getRouteClass());
		BusinessRoutingMethodRunner runner = new BusinessRoutingMethodRunner( configuration, requestContext, routeMethod, instance );
		ListenableCall listenable = listenable(runner);
		listenable.onSuccess(new RendererListener(configuration, requestContext));
		listenable.onSuccess(onSuccess);
		defineOnFail( listenable );
		return listenable;
	}

	public void defineOnFail( ListenableCall listenable ){
		for ( Listener<Exception> onFailListener : onFail )
			listenable.onFail(onFailListener);
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void onFail( Listener<Exception> listener ){
		this.onFail.add(listener);
	}

	protected void onFail( Exception cause ) {
		for ( Listener<Exception> onFailListener : onFail )
			onFailListener.listen(cause);
	}

	public void onSuccess( Listener<Object> listener ){
		this.onSuccess = listener;
	}
}

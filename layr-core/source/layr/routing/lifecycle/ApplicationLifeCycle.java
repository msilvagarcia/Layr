package layr.routing.lifecycle;

import layr.api.ApplicationContext;
import layr.api.RequestContext;
import layr.commons.Listener;
import layr.exceptions.NotFoundException;

public class ApplicationLifeCycle {

	Listener<Object> onSuccess;
	Listener<Exception> onFail;
	Listener<Exception> exceptionHandler;
	ClassInstantiationFactory classInstantiationFactory;

	public ApplicationLifeCycle() {
		super();
	}

	public void run(ApplicationContext applicationContext, RequestContext requestContext) throws Exception {
		createClassInstantiationFactory(applicationContext, requestContext);
		createRequestExceptionHandler(applicationContext, requestContext);
		LifeCycle[] lifeCycles = createLifeCycles(applicationContext, requestContext);

		for ( LifeCycle lifeCycle : lifeCycles )
			if ( lifeCycle.canHandleRequest() ){
				beforeRun();
				lifeCycle.run();
				afterRun();
				return;
			}

		throw new NotFoundException("Not found");
	}

	private void createClassInstantiationFactory(ApplicationContext applicationContext,
			RequestContext requestContext) {
		classInstantiationFactory = new ClassInstantiationFactory(applicationContext, requestContext);
	}

	protected void createRequestExceptionHandler(ApplicationContext applicationContext,
			RequestContext requestContext) {
		exceptionHandler = new ExceptionHandlerListener(applicationContext, requestContext, classInstantiationFactory);
	}

	protected LifeCycle[] createLifeCycles(ApplicationContext applicationContext, RequestContext requestContext) {
		LifeCycle[] lifeCycles = new LifeCycle[]{
			createBusinessRoutingLifeCycle(applicationContext, requestContext),
			createNaturalRoutingLifeCycle(applicationContext, requestContext)
		};
		return lifeCycles;
	}

	protected BusinessRoutingLifeCycle createBusinessRoutingLifeCycle(ApplicationContext applicationContext, RequestContext requestContext) {
		BusinessRoutingLifeCycle lifeCycle = new BusinessRoutingLifeCycle( applicationContext, requestContext, classInstantiationFactory );
		lifeCycle.onSuccess(onSuccess);
		lifeCycle.onFail(exceptionHandler);
		lifeCycle.onFail(onFail);
		return lifeCycle;
	}

	protected NaturalRoutingLifeCycle createNaturalRoutingLifeCycle(ApplicationContext applicationContext, RequestContext requestContext) {
		NaturalRoutingLifeCycle lifeCycle = new NaturalRoutingLifeCycle( applicationContext, requestContext );
		lifeCycle.onSuccess(onSuccess);
		lifeCycle.onFail(exceptionHandler);
		lifeCycle.onFail(onFail);
		return lifeCycle;
	}

	protected void beforeRun() {}
	protected void afterRun() {}

	public void onFail(Listener<Exception> listener) {
		this.onFail = listener;
	}

	public void onSuccess(Listener<Object> listener) {
		this.onSuccess = listener;
	}

}
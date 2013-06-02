package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import layr.api.Response;
import layr.commons.Listener;
import layr.exceptions.RoutingInitializationException;
import layr.routing.lifecycle.LifeCycle;
import layr.routing.lifecycle.NaturalRoutingLifeCycle;

import org.junit.Before;
import org.junit.Test;

public class NaturalRoutingLifeCycleTest extends RoutingTestSupport {

	static final int REPEAT_TIMES = 5;
	static final int MANY_TIMES = 1000;
	AtomicInteger failureCounter;
	CountDownLatch availableRequests;

	public LifeCycle createLifeCycle() throws RoutingInitializationException{
		NaturalRoutingLifeCycle lifeCycle = new NaturalRoutingLifeCycle( configuration, getRequestContext() );
		lifeCycle.onSuccess(new SuccessListener());
		lifeCycle.onFail(new FailListener());
		return lifeCycle;
	}
	
	@Before
	public void initializeFailureCounter(){
		failureCounter = new AtomicInteger(0);
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		availableRequests = new CountDownLatch(1);
		get( "/hello/?requestParam=12.5&pathParam=1234" );
		availableRequests.await();
		assertEquals( 0, failureCounter.get() );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test( timeout=1700 )
	public void stressTestFiveTimes() throws Exception{
		availableRequests = new CountDownLatch(REPEAT_TIMES*MANY_TIMES);
		for ( int i=0; i<REPEAT_TIMES; i++ )
			stressTest();
		availableRequests.await();
		assertEquals( 0, failureCounter.get() );
	}

	public void stressTest() throws Exception{
		for ( int i=0; i<MANY_TIMES; i++ ){
			recreateLifeCycle();
			get( "/hello/?requestParam=12.5&pathParam=1234" );
		}
	}

	@Override
	Set<Class<?>> classes() {
		return new HashSet<Class<?>>();
	}
	
	class SuccessListener implements Listener<Response> {
		@Override
		public void listen(Response result) {
			availableRequests.countDown();
		}
	}
	
	class FailListener implements Listener<Exception> {
		@Override
		public void listen(Exception result) {
			availableRequests.countDown();
			failureCounter.incrementAndGet();
			result.printStackTrace();
		}
	}
}

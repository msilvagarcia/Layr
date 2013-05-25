package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import layr.exceptions.RoutingInitializationException;
import layr.routing.lifecycle.LifeCycle;
import layr.routing.lifecycle.NaturalRoutingLifeCycle;

import org.junit.Test;

public class NaturalRoutingLifeCycleTest extends RoutingTestSupport {

	static final int MANY_TIMES = 1000;

	public LifeCycle createLifeCycle() throws RoutingInitializationException{
		return new NaturalRoutingLifeCycle( configuration, getRequestContext() );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		get( "/hello/?requestParam=12.5&pathParam=1234" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test( timeout=700 )
	public void stressTestFiveTimes() throws Exception{
		for ( int i=0; i<5; i++ )
			stressTest();
	}

	public void stressTest() throws Exception{
		for ( int i=0; i<MANY_TIMES; i++ ){
			get( "/hello/?requestParam=12.5&pathParam=1234" );
		}
	}

	@Override
	Set<Class<?>> classes() {
		return new HashSet<Class<?>>();
	}
}

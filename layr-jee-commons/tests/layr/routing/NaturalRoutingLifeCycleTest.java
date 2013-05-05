package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import layr.routing.impl.StubRoutingBootstrap;

import org.junit.Before;
import org.junit.Test;

public class NaturalRoutingLifeCycleTest extends RoutingTestSupport {

	static final int MANY_TIMES = 1000;
	Configuration configuration;

	public NaturalRoutingLifeCycleTest() throws RoutingInitializationException {
		RoutingBootstrap routingBootstrap = new StubRoutingBootstrap();
		configuration = routingBootstrap.configure( new HashSet<Class<?>>() );
	}

	@Before
	public void setup() throws RoutingInitializationException{
		lifeCycle = new NaturalRoutingLifeCycle( configuration );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		get( "/hello/?requestParamOnBody=12.5&pathParamOnBody=1234" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test( timeout=3300 )
	public void stressTestFiveTimes() throws Exception{
		for ( int i=0; i<5; i++ )
			stressTest();
	}

	public void stressTest() throws Exception{
		for ( int i=0; i<MANY_TIMES; i++ ){
			setup();
			get( "/hello/?requestParamOnBody=12.5&pathParamOnBody=1234" );
		}
	}
	
}

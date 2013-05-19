package layr.routing;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import layr.routing.api.ExceptionHandler;
import layr.routing.api.Handler;
import layr.routing.api.Response;
import layr.routing.lifecycle.HandlerClassExtractor;

public class HandlerClassExtractorTest {
	
	@SuppressWarnings("rawtypes")
	HandlerClassExtractor<ExceptionHandler> handlerClassExtractor;

	public HandlerClassExtractorTest() {
		handlerClassExtractor = HandlerClassExtractor.newInstance(ExceptionHandler.class);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void grantThatExtractDataAsExpected(){
		handlerClassExtractor.extract( ValidHandler.class );
		handlerClassExtractor.extract( InvalidHandler.class );
		Map<String, Class<ExceptionHandler>> handlers = handlerClassExtractor.getRegisteredHandlers();
		Assert.assertNotNull(handlers);
		Assert.assertEquals(1, handlers.size());
		Assert.assertEquals( ValidHandler.class, handlers.get(Throwable.class.getCanonicalName()) );
	}

}

interface FakeHandler {}
class InvalidHandler implements FakeHandler {}

@Handler
class ValidHandler implements ExceptionHandler<Throwable>, FakeHandler {

	@Override
	public Response render(Throwable exception) {
		return null;
	}
	
}

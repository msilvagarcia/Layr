package layr.routing;

import layr.routing.impl.StubRequestContext;
import layr.routing.lifecycle.TemplateParameterObjectHandler;
import layr.routing.sample.SampleTemplateObject;

import org.junit.Assert;
import org.junit.Test;

public class TemplateParameterObjectHandlerTest {
	
	private StubRequestContext context;
	private TemplateParameterObjectHandler handler;

	public TemplateParameterObjectHandlerTest() {
		context = new StubRequestContext();
		handler = new TemplateParameterObjectHandler(context);
	}

	@Test
	public void grantThatReadValidFieldsFromObjectAsExpected(){
		handler.memorizeParameters( new SampleTemplateObject("Helden", 01L, 0.123D) );
		Assert.assertNull( context.get("invalid") );
		Assert.assertEquals("Helden", context.get("name"));
		Assert.assertEquals(01L, context.get("age"));
	}
}

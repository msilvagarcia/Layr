package layr.routing;

import layr.routing.impl.StubRequestContext;
import layr.routing.lifecycle.BusinessRoutingTemplateParameterObjectHandler;
import layr.routing.sample.SampleTemplateObject;

import org.junit.Assert;
import org.junit.Test;

public class BusinessRoutingTemplateParameterObjectHandlerTest {
	
	private StubRequestContext context;
	private BusinessRoutingTemplateParameterObjectHandler handler;

	public BusinessRoutingTemplateParameterObjectHandlerTest() {
		context = new StubRequestContext();
		handler = new BusinessRoutingTemplateParameterObjectHandler(context);
	}

	@Test
	public void grantThatReadValidFieldsFromObjectAsExpected(){
		handler.memorizeParameters( new SampleTemplateObject("Helden", 01L, 0.123D) );
		Assert.assertNull( context.get("invalid") );
		Assert.assertEquals("Helden", context.get("name"));
		Assert.assertEquals(01L, context.get("age"));
	}
}

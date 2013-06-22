package layr.routing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import layr.api.RequestContext;
import layr.routing.lifecycle.TemplateParameterObjectHandler;

import org.junit.Test;

public class TemplateParameterObjectHandlerTest {
	
	private RequestContext context;
	private TemplateParameterObjectHandler handler;

	public TemplateParameterObjectHandlerTest() {
		context = mock(RequestContext.class);
		handler = new TemplateParameterObjectHandler(context);
	}

	@Test
	public void grantThatReadValidFieldsFromObjectAsExpected(){
		handler.memorizeParameters( new SampleTemplateObject("Helden", 01L, 0.123D) );
		verify(context).put("invalid", null);
		verify(context).put("name", "Helden");
		verify(context).put("age", 01L);
	}
	
	class SampleTemplateObject {

		private String name;
		public Long age;
		@SuppressWarnings("unused")
		private Double invalid;

		public SampleTemplateObject(String name, Long age, Double invalid) {
			this.name = name;
			this.age = age;
			this.invalid = invalid;
		}

		public String getName() {
			return name;
		}
	}
}

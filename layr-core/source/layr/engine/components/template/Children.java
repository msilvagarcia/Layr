package layr.engine.components.template;

import java.io.IOException;

import layr.api.Component;
import layr.engine.components.GenericComponent;


public class Children extends GenericComponent {

	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		Component children = retrieveMemorizedCurrentChildren();
		children.setRequestContext(getRequestContext());
		children.render();
	}

}

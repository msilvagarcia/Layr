package org.layr.engine.components.template;

import java.io.IOException;

import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.IComponent;

public class Children extends GenericComponent {

	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		IComponent children = retrieveMemorizedCurrentChildren();
		children.setRequestContext(getRequestContext());
		children.render();
	}

}

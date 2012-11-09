package org.layr.engine.components.template;

import java.io.IOException;

import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.IComponent;



public class TemplateBasedComponentChildren extends GenericComponent {

	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		IComponent children = (IComponent)getRequestContext().get(COMPONENT_CHILDREN);
		children.setRequestContext(getRequestContext());
		children.render();
	}

}

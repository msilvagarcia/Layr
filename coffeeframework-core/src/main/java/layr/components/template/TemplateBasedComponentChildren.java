package layr.components.template;

import java.io.IOException;

import layr.components.GenericComponent;
import layr.components.IComponent;


public class TemplateBasedComponentChildren extends GenericComponent {
	
	public static final String CHILDREN = "TemplateBasedComponentChildren.CHILDREN";

	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		IComponent children = (IComponent)getLayrContext().get(CHILDREN);
		children.setLayrContext(getLayrContext());
		children.render();
	}

}

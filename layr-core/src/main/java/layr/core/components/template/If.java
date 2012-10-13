package layr.core.components.template;

import java.io.IOException;

import layr.core.components.GenericComponent;
import layr.core.components.IComponent;


public class If extends GenericComponent {

	@Override
	public void render() throws IOException {

		Object test = getParsedAttribute("test");

		if ( test == null || 
			(Boolean.class.isInstance(test) && !((Boolean)test)))
			return;

		for (IComponent child : getChildren()) {
			child.setLayrContext(layrContext);
			child.render();
		}
	}
	
}

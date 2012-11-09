package org.layr.engine.components.template;

import java.io.IOException;

import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.IComponent;



public class If extends GenericComponent {

	@Override
	public void render() throws IOException {

		Object test = getParsedAttribute("test");

		if ( test == null || 
			(Boolean.class.isInstance(test) && !((Boolean)test)))
			return;

		for (IComponent child : getChildren()) {
			child.setRequestContext(requestContext);
			child.render();
		}
	}
	
}

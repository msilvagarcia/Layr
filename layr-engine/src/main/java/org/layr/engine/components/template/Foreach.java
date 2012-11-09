package org.layr.engine.components.template;

import java.io.IOException;
import java.util.Collection;

import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.IComponent;


public class Foreach extends GenericComponent {

	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		String string = getAttribute("var").toString();
		Object collection = getParsedAttribute("value");

		Collection<?> values = (Collection<?>)collection;
		if (values != null){
			int i = 0;
			for (Object value : values) {
				requestContext.put(string, value);
				requestContext.put(string+":i", i++);
				for (IComponent child : getChildren()) {
					child.setRequestContext(requestContext);
					child.render();
				}
			}
		}

	}
}

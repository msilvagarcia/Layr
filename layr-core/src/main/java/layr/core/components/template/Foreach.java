package layr.core.components.template;

import java.io.IOException;
import java.util.Collection;

import layr.core.components.GenericComponent;
import layr.core.components.IComponent;

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
				layrContext.put(string, value);
				layrContext.put(string+":i", i++);
				for (IComponent child : getChildren()) {
					child.setLayrContext(layrContext);
					child.render();
				}
			}
		}

	}
}

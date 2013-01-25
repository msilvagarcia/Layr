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
		String definedVar = getAttribute("var").toString();
		Object collection = getParsedAttribute("values");

		Collection<?> values = (Collection<?>)collection;
		if (values != null)
			iterateAndRenderChildrenNodes(definedVar, values);
	}

	public void iterateAndRenderChildrenNodes(String definedVar, Collection<?> values)
			throws IOException {
		int iterationCount = 0;
		for (Object value : values)
			iterationCount = renderChildNode(definedVar, iterationCount, value);
	}

	public int renderChildNode(String definedVar, int i, Object value)
			throws IOException {
		requestContext.put(definedVar, value);
		requestContext.put(definedVar+":i", i++);
		for (IComponent child : getChildren()) {
			child.setRequestContext(requestContext);
			child.render();
		}
		return i;
	}
}

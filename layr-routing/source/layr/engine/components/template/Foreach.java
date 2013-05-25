package layr.engine.components.template;

import java.io.IOException;
import java.util.Collection;

import layr.api.Component;
import layr.api.RequestContext;
import layr.engine.components.GenericComponent;



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
		RequestContext context = getRequestContext();
		context.put(definedVar, value);
		context.put(definedVar+":i", i++);
		for (Component child : getChildren()) {
			child.setRequestContext(context);
			child.render();
		}
		return i;
	}
}

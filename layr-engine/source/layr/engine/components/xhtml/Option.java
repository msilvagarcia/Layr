package layr.engine.components.xhtml;

import java.io.IOException;

import layr.engine.expressions.Evaluator;


public class Option extends XHtmlComponent {

	@Override
	public void configure() throws IOException {
		setComponentName("option");
		super.configure();
	}

	@Override
	public void render() throws IOException {
		String name = getParent().getAttributeAsString("name");
		Object value = new Evaluator(requestContext, "#{"+name+"}").eval();
		if (value == null || !value.equals(getAttributeAsString("value")))
			ignoreAttribute("selected");
		else
			setAttribute("selected","selected");
		super.render();
	}

}

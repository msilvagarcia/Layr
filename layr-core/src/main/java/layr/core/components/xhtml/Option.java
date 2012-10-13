package layr.core.components.xhtml;

import java.io.IOException;

import javax.servlet.ServletException;

import layr.core.expressions.ComplexExpressionEvaluator;


public class Option extends XHtmlComponent {

	@Override
	public void configure() throws ServletException, IOException {
		setComponentName("option");
		super.configure();
	}
	
	@Override
	public void render() throws IOException {
		String name = getParent().getAttributeAsString("name");
		Object value = ComplexExpressionEvaluator.getValue("#{"+name+"}", layrContext, true);
		if (value == null || !value.equals(getAttributeAsString("value")))
			ignoreAttribute("selected");
		else
			setAttribute("selected","selected");

		super.render();
	}

}

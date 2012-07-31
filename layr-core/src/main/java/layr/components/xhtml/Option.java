package layr.components.xhtml;

import java.io.IOException;

import javax.servlet.ServletException;

import layr.binding.ComplexExpressionEvaluator;


public class Option extends XHtmlComponent {

	@Override
	public void configure() throws ServletException, IOException {
		setComponentName("option");

		String name = getParent().getAttributeAsString("name");
		Object value = ComplexExpressionEvaluator.getValue("#{"+name+"}", layrContext, true);
		if (value == null || !value.equals(getAttributeAsString("value")))
			ignoreAttribute("selected");
		else
			setAttribute("selected","selected");

		super.configure();
	}

}

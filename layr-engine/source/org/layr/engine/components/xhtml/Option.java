package org.layr.engine.components.xhtml;

import java.io.IOException;

import javax.servlet.ServletException;

import org.layr.engine.expressions.Evaluator;

public class Option extends XHtmlComponent {

	@Override
	public void configure() throws ServletException, IOException {
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

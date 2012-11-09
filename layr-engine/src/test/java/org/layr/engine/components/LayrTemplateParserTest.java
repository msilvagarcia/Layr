package org.layr.engine.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.layr.engine.IRequestContext;
import org.layr.engine.TemplateParser;
import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.IComponent;
import org.layr.engine.sample.StubRequestContext;
import org.xml.sax.SAXException;

public class LayrTemplateParserTest {

	private IRequestContext layrContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		layrContext = new StubRequestContext();
	}

	@Test
	public void parseHelloWorldTemplate() throws IOException, ParserConfigurationException, SAXException {
		InputStream template = layrContext.getResourceAsStream("templates/hello.xhtml");
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse(template);
		assertNotNull(application);
	}

	@Test
	public void grantThatNestingDontLooseTexts() throws IOException, ParserConfigurationException, SAXException {
		InputStream template = layrContext.getResourceAsStream("templates/nestedTest.xhtml");
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse(template);
		assertNotNull(application);
		assertEquals(3, application.getNumChildren());
		assertEquals(" Home!", application.getChildren().get(2).getTextContent());
	}

	@Test
	public void grantThatComponentNestingWorkWithGenericComponent() throws
			IOException, ParserConfigurationException, SAXException {
		InputStream template = layrContext.getResourceAsStream("templates/genericComponentTemplate.xhtml");
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse(template);

		assertNotNull(application);
		assertEquals(1, application.getNumChildren());
		
		IComponent paragraph = application.getChildren().get(0);
		assertEquals(GenericComponent.class,
				paragraph.getClass());
		assertEquals("Paragraph", ((GenericComponent)paragraph).getComponentName());
	}

}

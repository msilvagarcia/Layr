package layr.core.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import layr.core.ApplicationContext;
import layr.core.RequestContext;
import layr.core.TemplateParser;
import layr.core.components.GenericComponent;
import layr.core.components.IComponent;
import layr.core.test.stubs.StubsFactory;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;



public class LayrTemplateParserTest {

	private RequestContext layrContext;
	private ApplicationContext applicationContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		layrContext = StubsFactory.createFullRequestContext();
		applicationContext = layrContext.getApplicationContext();
	}

	@Test
	public void parseHelloWorldTemplate() throws IOException, ParserConfigurationException, SAXException {
		InputStream template = applicationContext.getResourceAsStream("templates/hello.xhtml");
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse(template);
		assertNotNull(application);
	}

	@Test
	public void grantThatNestingDontLooseTexts() throws IOException, ParserConfigurationException, SAXException {
		InputStream template = applicationContext.getResourceAsStream("templates/nestedTest.xhtml");
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse(template);
		assertNotNull(application);
		assertEquals(3, application.getNumChildren());
		assertEquals(" Home!", application.getChildren().get(2).getTextContent());
	}

	@Test
	public void grantThatComponentNestingWorkWithGenericComponent() throws
			IOException, ParserConfigurationException, SAXException {
		InputStream template = applicationContext.getResourceAsStream("templates/genericComponentTemplate.xhtml");
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

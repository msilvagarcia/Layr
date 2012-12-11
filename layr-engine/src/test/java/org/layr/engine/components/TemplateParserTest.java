package org.layr.engine.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.layr.commons.FileUtils;
import org.layr.engine.IRequestContext;
import org.layr.engine.TemplateParser;
import org.layr.engine.sample.StubRequestContext;
import org.xml.sax.SAXException;

public class TemplateParserTest {

	private IRequestContext layrContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		layrContext = new StubRequestContext();
	}

	@Test
	public void parseHelloWorldTemplate() throws IOException, ParserConfigurationException, SAXException {
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse("templates/hello.xhtml");
		assertNotNull(application);
	}

	@Test
	public void grantThatNestingDontLooseTexts() throws IOException, ParserConfigurationException, SAXException {
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse("templates/nestedTest.xhtml");
		assertNotNull(application);
		assertEquals(3, application.getNumChildren());
		assertEquals(" Home!", application.getChildren().get(2).getTextContent());
	}

	@Test
	public void grantThatComponentNestingWorkWithGenericComponent() throws
			IOException, ParserConfigurationException, SAXException {
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent application = parser.parse("templates/genericComponentTemplate.xhtml");

		assertNotNull(application);
		assertEquals(1, application.getNumChildren());

		IComponent paragraph = application.getChildren().get(0);
		assertEquals(GenericComponent.class,
				paragraph.getClass());
		assertEquals("Paragraph", ((GenericComponent)paragraph).getComponentName());
	}

	@Test
	public void grantThatParseTwoLevelNestedComponentsAsExpected() throws TemplateParsingException, IOException{
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent compiledSnippets = parser.compile("templates/twoLevelNestedSnippet.xhtml");
		assertEquals("templates/twoLevelNestedSnippet.xhtml", compiledSnippets.getSnippetName());

		compiledSnippets.render();
		
		String output = ((StubRequestContext)layrContext).getBuffedWroteContentToOutput();
		String expectedOutput = FileUtils.readFileAsString("templates/twoLevelNestedSnippet.output.xhtml");
		assertEquals( expectedOutput, output );
	}

	@Test
	public void grantThatDoentThrowsExceptionWhenTryToCompileUnknownTemplateFile() throws TemplateParsingException{
		TemplateParser parser = new TemplateParser(layrContext);
		parser.compile("unknownLocation/template.xhtml");
	}

	@Test
	public void grantThatParseTwoLevelInheritenceTemplateAsExpected() throws TemplateParsingException, IOException{
		TemplateParser parser = new TemplateParser(layrContext);
		IComponent compiledSnippets = parser.compile("templates/twoLevelTemplateInheritence.xhtml");
		compiledSnippets.render();
		
		String output = ((StubRequestContext)layrContext).getBuffedWroteContentToOutput();
		String expectedOutput = FileUtils.readFileAsString("templates/twoLevelTemplateInheritence.output.xhtml");
		assertEquals( expectedOutput, output );
	}
}

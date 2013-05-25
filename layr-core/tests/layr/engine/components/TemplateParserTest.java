package layr.engine.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import layr.api.Component;
import layr.api.RequestContext;
import layr.commons.FileUtils;
import layr.engine.TemplateParser;
import layr.routing.impl.StubRequestContext;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TemplateParserTest {

	private RequestContext requestContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException {
		requestContext = new StubRequestContext();
	}

	@Test
	public void parseHelloWorldTemplate() throws IOException, ParserConfigurationException, SAXException {
		TemplateParser parser = new TemplateParser(requestContext);
		Component application = parser.parse("templates/hello.xhtml");
		assertNotNull(application);
	}

	@Test
	public void grantThatNestingDontLooseTexts() throws IOException, ParserConfigurationException, SAXException {
		TemplateParser parser = new TemplateParser(requestContext);
		Component application = parser.parse("templates/nestedTest.xhtml");
		assertNotNull(application);
		assertEquals(3, application.getNumChildren());
		assertEquals(" Home!", application.getChildren().get(2).getTextContent());
	}

	@Test
	public void grantThatComponentNestingWorkWithGenericComponent() throws
			IOException, ParserConfigurationException, SAXException {
		TemplateParser parser = new TemplateParser(requestContext);
		Component application = parser.parse("templates/genericComponentTemplate.xhtml");

		assertNotNull(application);
		assertEquals(1, application.getNumChildren());

		Component paragraph = application.getChildren().get(0);
		assertEquals(GenericComponent.class,
				paragraph.getClass());
		assertEquals("Paragraph", ((GenericComponent)paragraph).getComponentName());
	}

	@Test
	public void grantThatParseTwoLevelNestedComponentsAsExpected() throws TemplateParsingException, IOException{
		TemplateParser parser = new TemplateParser(requestContext);
		Component compiledSnippets = parser.compile("templates/twoLevelNestedSnippet.xhtml");
		assertEquals("templates/twoLevelNestedSnippet.xhtml", compiledSnippets.getSnippetName());

		compiledSnippets.render();
		
		String output = getRenderedOutput();
		String expectedOutput = FileUtils.readFileAsString("templates/twoLevelNestedSnippet.output.xhtml");
		assertEquals( expectedOutput, output );
	}

	@Test
	public void grantThatDoentThrowsExceptionWhenTryToCompileUnknownTemplateFile() throws TemplateParsingException{
		TemplateParser parser = new TemplateParser(requestContext);
		parser.compile("unknownLocation/template.xhtml");
	}

	@Test
	public void grantThatParseTwoLevelInheritenceTemplateAsExpected() throws TemplateParsingException, IOException{
		TemplateParser parser = new TemplateParser(requestContext);
		Component compiledSnippets = parser.compile("templates/twoLevelTemplateInheritence.xhtml");
		compiledSnippets.render();
		
		String output = getRenderedOutput();
		String expectedOutput = FileUtils.readFileAsString("templates/twoLevelTemplateInheritence.output.xhtml");
		assertEquals( expectedOutput, output );
	}
	
	@Test
	public void grantThatHomeScreenInheritDefaultThemeAsExpected() throws IOException, TemplateParsingException {
		requestContext.put("comments", "Its a comment.");
		TemplateParser parser = new TemplateParser(requestContext);
		Component compiledSnippet = parser.compile("templates/homeScreenThatInheritDefaultTheme.xhtml");
		compiledSnippet.render();
		
		String output = getRenderedOutput();
		String expectedOutput = FileUtils.readFileAsString("templates/homeScreenThatInheritDefaultTheme.output.xhtml");
		assertEquals( expectedOutput, output );
	}

	public String getRenderedOutput() {
		return ((StubRequestContext)requestContext).getBufferedWroteContentToOutput();
	}
}

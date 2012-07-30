package layr.bundler;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.Assert;
import layr.ApplicationContext;
import layr.RequestContext;
import layr.bundler.TemplateBundler;
import layr.test.CoffeeLifeCycleTestFactory;

import org.junit.Before;
import org.junit.Test;


public class TemplateBundlerTest {

	private RequestContext coffeeContext;
	private ApplicationContext applicationContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		coffeeContext = CoffeeLifeCycleTestFactory.createFullRequestContext();
		applicationContext = coffeeContext.getApplicationContext();
	}
	
	@Test
	public void assertThatItBundledBasedOnTemplate() throws IOException {
		TemplateBundler bundler = new TemplateBundler(coffeeContext);
		bundler.setTemplate("template.js");
		bundler.setRootDirectory("/");

		String buffer = bundler.bundleFile("jsapplication/Sample.js");
		String expectedBuffer = applicationContext.getResourceAsStringBuilder("output.js").toString();
		Assert.assertEquals(expectedBuffer, buffer);
	}

	@Test
	public void assertThatItBundledDirectoryBasedOnTemplate() throws IOException {
		TemplateBundler bundler = new TemplateBundler(coffeeContext);
		bundler.setTemplate("template.js");
		bundler.setRootDirectory("/");
		
		StringBuilder buffer = bundler.bundleDir("jsapplication");
		StringBuilder expectedBuffer = applicationContext.getResourceAsStringBuilder("outputDirectory.js");
		Assert.assertEquals(expectedBuffer.toString(), buffer.toString());
	}

	@Test
	public void assertThatItBundledDirectoryBasedOnTemplateAndWithRootDirectory() throws IOException {
		TemplateBundler bundler = new TemplateBundler(coffeeContext);
		bundler.setTemplate("template.js");
		bundler.setRootDirectory("/jsapplication");
		
		StringBuilder buffer = bundler.bundleDir("/");
		StringBuilder expectedBuffer = applicationContext.getResourceAsStringBuilder("outputWithRootDirectory.js");
		Assert.assertEquals(expectedBuffer.toString(), buffer.toString());
	}

	@Test
	public void assertThatItBundledDirectoryWithoutTemplateAndWithRootDirectory() throws IOException {
		TemplateBundler bundler = new TemplateBundler(coffeeContext);
		bundler.setRootDirectory("/jsapplication");

		StringBuilder buffer = bundler.bundleDir("/");
		StringBuilder expectedBuffer = applicationContext.getResourceAsStringBuilder("outputForEmptyTemplateButDirectorySet.js");
		Assert.assertEquals(expectedBuffer.toString(), buffer.toString());
	}

	@Test
	public void assertThatItBundledDirectoryBasedOnTemplateAndFilePattern() throws IOException {
		TemplateBundler bundler = new TemplateBundler(coffeeContext);
		bundler.setRootDirectory("/");
		bundler.setFileNamePattern(".*\\.js$");
		
		StringBuilder buffer = bundler.bundleDir("app");
		StringBuilder expectedBuffer = applicationContext.getResourceAsStringBuilder("outputPatternSearchBundled.js");
		Assert.assertEquals(expectedBuffer.toString(), buffer.toString());
	}

	@Test
	public void assertThatItBundledDirectoryBasedOnTemplateAndFilePatternButIgnoreExcludedFile() throws IOException {
		TemplateBundler bundler = new TemplateBundler(coffeeContext);
		bundler.setRootDirectory("/");
		bundler.setFileNamePattern(".*\\.js$");
		bundler.getExcludedFiles().add(".*/Hello.*");
		
		StringBuilder buffer = bundler.bundleDir("app");
		StringBuilder expectedBuffer = applicationContext.getResourceAsStringBuilder("outputPatternSearchBundledIgnoringHello.js");
		Assert.assertEquals(expectedBuffer.toString(), buffer.toString());
	}

	@Test
	public void assertThatReadsAFile() throws IOException {
		applicationContext.getResourceAsStringBuilder("output.js").toString();
	}

}

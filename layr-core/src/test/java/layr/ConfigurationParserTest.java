package layr;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import layr.test.LifeCycleTestFactory;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;


public class ConfigurationParserTest {
	
	private ApplicationContext applicationContext;

	@Before
	public void setup () throws IOException, ClassNotFoundException, ServletException{
		RequestContext requestContext = LifeCycleTestFactory.createFullRequestContext();
		applicationContext = requestContext.getApplicationContext();
	}

	@Test
	public void grantThatItParseConfigurationWell() throws IOException, ParserConfigurationException, SAXException{

		InputStream stream = applicationContext.getResourceAsStream("META-INF/layr.xml");
		Configuration configuration = Configuration.parse(stream);

		Assert.assertFalse(configuration.isEquationsDisabled());
		Assert.assertFalse(configuration.isCacheEnabled());
		Assert.assertEquals("/home", configuration.getDefaultResource());
	}

	@Test
	public void grantThatExecuteItFiveHoundredTimes() throws IOException, ParserConfigurationException, SAXException {
		for ( int i=0; i<500; i++) {
			grantThatItParseConfigurationWell();
		}
	}

}

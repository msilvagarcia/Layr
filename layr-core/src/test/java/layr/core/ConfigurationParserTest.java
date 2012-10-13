package layr.core;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import layr.core.Configuration;

import org.junit.Test;
import org.xml.sax.SAXException;

public class ConfigurationParserTest {

	@Test
	public void testConfigurationDefaultValues() throws IOException, ParserConfigurationException, SAXException {
		Configuration configuration = new Configuration();
		Assert.assertFalse(configuration.isEquationsDisabled());
		Assert.assertFalse(configuration.isCacheEnabled());
		Assert.assertEquals("/theme/", configuration.getDefaultResource());
	}

	@Test
	public void testConfigurationSettedValues() throws IOException, ParserConfigurationException, SAXException {
		System.setProperty("layr.config.equationDisabled", "true");
		System.setProperty("layr.config.cacheEnabled", "true");
		System.setProperty("layr.config.defaultResource", "/home/");

		Configuration configuration = new Configuration();
		Assert.assertTrue(configuration.isEquationsDisabled());
		Assert.assertTrue(configuration.isCacheEnabled());
		Assert.assertEquals("/home/", configuration.getDefaultResource());
	}

}

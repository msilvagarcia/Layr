package layr;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Configuration extends DefaultHandler {

	private StringBuffer textContent;
	
	private boolean isCacheEnabled;
	private boolean isEquationsDisabled;
	private String defaultResource;

	public Configuration() {
		this.textContent = new StringBuffer();
		this.isEquationsDisabled = false;
		this.isCacheEnabled = true;
		this.defaultResource = "/home/";
	}

	/**
	 * 
	 * @param configurationFile
	 * @return a parsed layr configuration
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Configuration parse(InputStream configurationFile)
			throws ParserConfigurationException, SAXException, IOException {
		
		Configuration configurationParser = new Configuration();
		
		SAXParserFactory sax = SAXParserFactory.newInstance();
		sax.setValidating(false);
		sax.setNamespaceAware(true);

		SAXParser parser = sax.newSAXParser();
		parser.parse(configurationFile, configurationParser);

		return configurationParser;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		textContent.delete(0, textContent.length());

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		textContent.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
 
		if ( localName.equals("cacheEnabled") ) {
			isCacheEnabled = Boolean.parseBoolean(textContent.toString());
		} else if ( localName.equals("equationsDisabled") ) {
			isEquationsDisabled = Boolean.parseBoolean(textContent.toString());
		} else if ( localName.equals("defaultResource") ) {
			defaultResource = textContent.toString();
		}

		textContent.delete(0, textContent.length());
	}

	public Boolean isCacheEnabled() {
		return isCacheEnabled;
	}

	public void setIsCacheEnabled(Boolean isCacheEnabled) {
		this.isCacheEnabled = isCacheEnabled;
	}

	public String getDefaultResource() {
		return defaultResource;
	}

	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}

	public boolean isEquationsDisabled() {
		return isEquationsDisabled;
	}

	public void setEquationsDisabled(boolean isEquationsDisabled) {
		this.isEquationsDisabled = isEquationsDisabled;
	}
}

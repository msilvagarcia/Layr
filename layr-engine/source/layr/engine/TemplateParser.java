/**
 * Copyright 2013 Miere Liniel Teixeira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package layr.engine;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import layr.commons.StringUtil;
import layr.engine.components.Component;
import layr.engine.components.ComponentFactory;
import layr.engine.components.DefaultComponentFactory;
import layr.engine.components.TemplateParsingException;
import layr.engine.components.TextNode;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TemplateParser extends DefaultHandler {

	public static final String EMPTY_DOCTYPE = "<!DOCTYPE %s>";

	private Component rootComponent;
	private Component currentComponent;
	private StringBuffer textContent;
	private RequestContext requestContext;
	private String doctype;
	private String currentTemplateName;

	/**
	 * @param context
	 */
	public TemplateParser(RequestContext context) {
		this.textContent = new StringBuffer();
		setRequestContext(context);
	}

	/**
	 * @param templateName
	 * @return
	 * @throws TemplateParsingException
	 */
	public Component compile(String templateName) throws TemplateParsingException {
		try{
			Component component = requestContext.getResourceFromCache( templateName );
			if (component != null)
				return (Component) component.clone(requestContext);
			component = parse(templateName);
			cacheCompiledResource(templateName, component);
			return component;
		} catch (Throwable e){
			throw new TemplateParsingException("Can't parse '" + templateName + "' as XHTML.", e);
		}
	}

	/**
	 * @param templateName
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Component parse(String templateName) throws ParserConfigurationException, SAXException, IOException {
		setCurrentTemplateName(templateName);
		Component parsedComponent = parse( requestContext.getResourceAsStream(templateName) );
		if ( parsedComponent != null && !StringUtil.isEmpty( getDoctype() ) )
			parsedComponent.setDocTypeDefinition(getDoctype());
		return parsedComponent;
	}

	/**
	 * @param template
	 * @param templateName 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Component parse(InputStream template) throws ParserConfigurationException, SAXException, IOException {
		if ( template == null )
			return null;

		SAXParserFactory sax = SAXParserFactory.newInstance();
		sax.setValidating(false);
		sax.setNamespaceAware(true);

		SAXParser parser = sax.newSAXParser();
		parser.parse(template, this);

		return rootComponent;
	}

	/**
	 * @param templateName
	 * @param component
	 */
	public void cacheCompiledResource(String templateName, Component component) {
		if ( component != null )
			requestContext.cacheCompiledResource(templateName, component);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		if (publicId == null || systemId == null)
			setDoctype(EMPTY_DOCTYPE);
		else
			setDoctype("<!DOCTYPE %s PUBLIC \"" + publicId + "\" \""
					+ systemId + "\">");
		return new org.xml.sax.InputSource(new java.io.StringReader(""));
	}

	@Override
	/**
	 * Stores the namespace for future component initialization.
	 */
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if (requestContext.isRegisteredNamespace(uri))
			return;

		DefaultComponentFactory factory = new DefaultComponentFactory();
		factory.setRootDir(StringUtil.join(uri.replace("urn:", "").split(":"), "/"));
		requestContext.registerNamespace(uri, factory);
	}

	@Override
	/**
	 * Creates the current element and set its attributes. At same time it
	 * stores the current component at child hierarchy.
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		try {
			extractTextContentBeforeNesting();
			Component newComponent = createNewComponent(uri, localName, qName);
			setFoundAttributesToComponent(newComponent, attributes);
			defineCurrentParsedComponent(newComponent);
		} catch (Exception e) {
			throw new SAXException(e.getMessage(), e);
		}
	}

	/**
	 * @param newComponent
	 */
	public void defineCurrentParsedComponent(Component newComponent) {
		if (currentComponent != null) {
			newComponent.setParent(currentComponent);
			currentComponent.addChild(newComponent);
		} else
			rootComponent = newComponent;
		currentComponent = newComponent;
	}

	/**
	 * @param newComponent
	 * @param attributes
	 */
	public void setFoundAttributesToComponent(Component newComponent, Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++)
			newComponent.setAttribute(attributes.getQName(i),
					attributes.getValue(i));
	}

	/**
	 * @param uri
	 * @param localName
	 * @param qName
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Component createNewComponent(String uri, String localName, String qName)
			throws InstantiationException, IllegalAccessException {
		ComponentFactory factory = requestContext.getComponentFactory(uri);
		Component newComponent = factory.newComponent(
				localName, qName, requestContext);
		
		if ( newComponent != null )
			newComponent.setSnippetName(currentTemplateName);
		return newComponent;
	}

	/**
	 * Before nesting the next components, it's needed to save some text nodes
	 * to be rendered correctly in the HTML web page.
	 */
	public void extractTextContentBeforeNesting() {
		String string = textContent.toString();
		if (currentComponent != null
				&& !string.replaceAll("[\\n\\r]", "").trim().isEmpty()){
			currentComponent.addChild( createTextNode(string) );
			currentComponent.setTextContent(string);
		}
		textContent.delete(0, textContent.length());
	}

	/**
	 * @param string
	 * @return
	 */
	public TextNode createTextNode( String string ) {
		TextNode textNode = new TextNode(string);
		textNode.setRequestContext(getRequestContext());
		return textNode;
	}

	@Override
	/**
	 * After parse a child element it stores the parentChild to the currentComponent
	 * and sets the textContent, if any.
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			extractTextContentBeforeNesting();
			currentComponent.configure();
			currentComponent = currentComponent.getParent();
		} catch (IOException e) {
			throw new SAXException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		textContent.append(ch, start, length);
	}

	/**
	 * @param rootComponent
	 */
	public void setRootComponent(Component rootComponent) {
		this.rootComponent = rootComponent;
	}

	/**
	 * @return
	 */
	public Component getRootComponent() {
		return rootComponent;
	}

	/**
	 * @return
	 */
	public RequestContext getRequestContext() {
		return requestContext;
	}

	/**
	 * @param layrContext
	 */
	public void setRequestContext(RequestContext layrContext) {
		this.requestContext = layrContext;
	}

    /**
     * @return the doctype
     */
    public String getDoctype() {
        return doctype;
    }

    /**
     * @param doctype the doctype to set
     */
    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

	/**
	 * @return
	 */
	public String getCurrentTemplateName() {
		return currentTemplateName;
	}

	/**
	 * @param currentTemplateName
	 */
	public void setCurrentTemplateName(String currentTemplateName) {
		this.currentTemplateName = currentTemplateName;
	}

}

/**
 * Copyright 2011 Miere Liniel Teixeira
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
package layr;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import layr.components.DefaultComponentFactory;
import layr.components.IComponent;
import layr.components.IComponentFactory;
import layr.components.TextNode;
import layr.util.StringUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @since Layr 1.0
 */
public class TemplateParser extends DefaultHandler {

	private IComponent rootComponent;
	private IComponent currentComponent;
	private StringBuffer textContent;
	private RequestContext layrContext;
	private String doctype;

	public TemplateParser(RequestContext context) {
		this.textContent = new StringBuffer();
		setLayrContext(context);
	}

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public IComponent parse(InputStream template)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory sax = SAXParserFactory.newInstance();
		sax.setValidating(false);
		sax.setNamespaceAware(true);

		SAXParser parser = sax.newSAXParser();
		parser.parse(template, this);

		return rootComponent;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		if (publicId == null || systemId == null)
			setDoctype("<!DOCTYPE html>");
		else
			setDoctype("<!DOCTYPE html PUBLIC \"" + publicId + "\" \""
					+ systemId + "\">");
		return new org.xml.sax.InputSource(new java.io.StringReader(""));
	}

	@Override
	/**
	 * Stores the namespace for future component initialization.
	 */
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if (layrContext.isRegisteredNamespace(uri))
			return;

		DefaultComponentFactory factory = new DefaultComponentFactory();
		factory.setRootDir(StringUtil.join(uri.replace("urn:", "").split(":"), "/"));

		layrContext.registerNamespace(uri, factory);
	}

	@Override
	/**
	 * Creates the current element and set its attributes. At same time it
	 * stores the current component at child hierarchy.
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		extractTextContentBeforeNesting();

		try {
			IComponentFactory factory = layrContext.getComponentFactory(uri);
			IComponent newComponent = factory.newComponent(
					localName, qName, layrContext);

			if (newComponent == null)
				throw new SAXException("Can't parse the unknown element '"
						+ localName + "'");

			for (int i = 0; i < attributes.getLength(); i++) {
				newComponent.setAttribute(attributes.getQName(i),
						attributes.getValue(i));
			}

			if (currentComponent != null) {
				newComponent.setParent(currentComponent);
				currentComponent.addChild(newComponent);
			} else
				rootComponent = newComponent;
			currentComponent = newComponent;

		} catch (InstantiationException e) {
			throw new SAXException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new SAXException(e.getMessage(), e);
		}
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

	public TextNode createTextNode( String string ) {
		TextNode textNode = new TextNode(string);
		textNode.setLayrContext(getLayrContext());
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
		} catch (ServletException e) {
			throw new SAXException(e.getMessage(), e);
		} catch (IOException e) {
			throw new SAXException(e.getMessage(), e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		textContent.append(ch, start, length);
	}

	public void setRootComponent(IComponent rootComponent) {
		this.rootComponent = rootComponent;
	}

	public IComponent getRootComponent() {
		return rootComponent;
	}

	public RequestContext getLayrContext() {
		return layrContext;
	}

	public void setLayrContext(RequestContext layrContext) {
		this.layrContext = layrContext;
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

}

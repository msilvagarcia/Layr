/**
 * Copyright 2012 Miere Liniel Teixeira
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
package layr.components;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;

import layr.LayrFactory;
import layr.ApplicationContext;
import layr.RequestContext;
import layr.binding.ComplexExpressionEvaluator;
import layr.commons.StringUtil;
import layr.components.template.HolderComponent;
import layr.components.template.TemplateBasedComponentChildren;

import org.xml.sax.SAXException;



/**
 * Default implementation for components. Software developers can override the
 * methods for a custom component development. The <b><i>See Also</i></b>
 * section has useful methods JavaDocs to understand the basics for construct
 * your self custom component.
 * 
 * @author Miere Liniel Teixeira
 * @see IComponent#clone()
 * @see IComponent#getChildren()
 * @see IComponent#getId()
 * @see IComponent#render()
 */
public class GenericComponent implements IComponent {

	private static final String[] styles = new String[] {
		"align","border","background","height","display","min-height","max-height", "width",
		"max-width","min-width","top","left","position","margin","padding","bottom","right"
	};

	private final static String[] events = new String[] {
		"blur", "change", "click",
		"dblclick", "focus", "mousedown", "mousemove",
		"mouseout", "mouseover", "mouseup", "keydown",
		"keypress", "keyup", "select" };

	protected List<IComponent> children;
	private Map<String, Object> attributes;
	private IComponent parent;
	private String textContent;
	private String id;
	protected RequestContext layrContext;

	private String rootdir = "";
	private String extension = "xhtml";
	private String componentName;
	private String qualifiedName;

	public GenericComponent() {
		super();
		children = new ArrayList<IComponent>();
		attributes = new HashMap<String, Object>();
	}

	public void configure() throws ServletException, IOException {
		setStylesDefinition();
	}

	/**
	 * Group all styles in "styles" component attribute
	 */
	public void setStylesDefinition() {
		StringBuilder buffer = new StringBuilder();
		
		for (String style : styles) {
			String value = getAttributeAsString(style);
			if (StringUtil.isEmpty(value))
				continue;
			
			buffer.append(style)
				.append(':')
				.append(value)
				.append(';');
		}

		attributes.put("styles", buffer.toString());
	}

	/**
	 * Based on {@link ArrayList#contains(Object)} implementation.
	 * @param object
	 * @return
	 */
	public boolean isValidEvent(String object) {
		for (int i = 0; i < events.length; i++) {
			if (object.equals(events[i])) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see layr.components.IComponent#render()
	 */
	@Override
	public void render() throws IOException {
		try {

			HolderComponent holder = new HolderComponent();
			holder.setChildren(getChildren());
			holder.configure();
			RequestContext layrContext = getLayrContext();
			layrContext.put(TemplateBasedComponentChildren.CHILDREN, holder);

			ApplicationContext applicationContext = LayrFactory
					.getOrCreateApplicationContext(layrContext);
			IComponent compiledTemplate = 
					applicationContext.compile(getTemplate(), layrContext);

			if ( compiledTemplate == null ){
				//throw new IOException("Can't find template '" + getTemplate() + "'.");
				renderAsComponentStub();
				return;
			}

			renderCompiledTemplate(compiledTemplate);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Render component as stub.
	 * @throws IOException 
	 */
	public void renderAsComponentStub() throws IOException {
		PrintWriter writer = layrContext.getResponse().getWriter();
		writer.append('<')
			  .append(getQualifiedName())
			  .append(' ');
		
		for ( String attribute: getAttributeKeys() )
			writer.append( attribute )
				  .append( "=\"" )
				  .append( getAttributeAsString(attribute) )
				  .append( "\" " );
		
		List<IComponent> children = getChildren();
		if ( children == null || children.size() == 0 ){
			writer.append("/>");
			return;
		}

		writer.append('>');
		for ( IComponent child : children )
			child.render();
		writer.append("</")
			  .append(getQualifiedName())
			  .append('>');
	}

	/**
	 * Render compiled template.
	 * 
	 * @param compiledTemplate
	 * @throws IOException
	 */
	public void renderCompiledTemplate(IComponent compiledTemplate) throws IOException {

		ArrayList<String> attributes = new ArrayList<String>();
		for (String attribute : getAttributeKeys()) {
			getLayrContext().put(getComponentName() + ":" + attribute, getParsedAttribute(attribute));
			attributes.add(getComponentName() + ":" + attribute);
		}

		compiledTemplate.render();

		for (String attribute : attributes)
			getLayrContext().put(attribute, null);
	}

	/**
	 * @return
	 */
	public String getTemplate() {
		return getRootdir() + "/" + getComponentName() + "." + getExtension();
	}

	/* (non-Javadoc)
	 * @see layr.components.IComponent#clone(layr.RequestContext)
	 */
	@Override
	public Object clone(RequestContext context)
			throws CloneNotSupportedException, ServletException, IOException {
		flush();

		List<IComponent> children = new ArrayList<IComponent>();
		GenericComponent clone = (GenericComponent) super.clone();
		clone.setLayrContext(context);

		for (IComponent child : getChildren()) {
			child.setParent(clone);
			children.add((IComponent) child.clone(context));
		}

		clone.setChildren(children);
		clone.setTextContent(textContent);
		clone.configure();

		return clone;
	}

	/**
	 * Flushes objects that will be never used after component's rendering.
	 * Custom components should override this method to clean some variables to
	 * avoid memory leaks. Note that you should always call super.flush() to
	 * clean the {@link GenericComponent} internal variables.
	 */
	protected void flush() {
		this.setLayrContext(null);
	}

	@Override
	public void addChild(IComponent child) {
		children.add(child);
	}

	/**
	 * Insert a child on the children list at position defined by <i>index</i>
	 * parameter.
	 * 
	 * @param child
	 * @param index
	 */
	public void addChild(IComponent child, int index) {
		children.add(index, child);
	}

	@Override
	public IComponent setTextContent(String content) {
		this.textContent = content;
		return this;
	}

    @Override
	public String getTextContent() {
		if (textContent == null)
			return null;

		return (String) ComplexExpressionEvaluator.getValue(textContent, getLayrContext(), true);
	}
    
    public String getNonParsedTextContent() {
    	return textContent;
    }

	@Override
	/**
	 * Retrieves the component's attribute value as an Object.<br/>
	 * <br/>
	 * If the component implementation has a setter method with same name of the attribute
	 * it will be dispatched and ignoring the value binding. Otherwise, it will try to
	 * retrieve the value from a possible defined binding expression.
	 * 
	 * @param attribute
	 */
	public Object getAttribute(String attribute) {
		return attributes.get(attribute);
	}
	
	/**
	 * @param attribute
	 * @return
	 */
	public Object getParsedAttribute(String attribute) {
		return getParsedAttribute(attribute, false);
	}

	/**
	 * Retrieves the attribute value and, if it is an valid expression, returns
	 * the value of the parsed expression
	 * 
	 * @param attribute
	 * @return
	 */
	public Object getParsedAttribute(String attribute, boolean shouldBeEncoded) {
		Object object = attributes.get(attribute);
		if (object == null)
			return null;
		return ComplexExpressionEvaluator.getValue(object.toString(), getLayrContext(), shouldBeEncoded);
	}

	/**
	 * Retrieves the component's attribute value as an String.
	 * 
	 * @param attr
	 * @return
	 */
	public String getAttributeAsString(String attr) {
		Object value = getParsedAttribute(attr, true);
		if (value == null)
			return "";

		if ( String.class.isInstance(value) )
			return StringUtil.escape((String)value);

		return value.toString();
	}

	@Override
	/**
	 * Sets an attribute value.<br/>
	 * <br/>
	 * After stores the value, if the component implementation has a setter method with same name of the attribute
	 * it will be dispatched too.
	 */
	public IComponent setAttribute(String attribute, Object value) {
		this.attributes.put(attribute, value);
		return this;
	}

	public Collection<String> getAttributeKeys() {
		return attributes.keySet();
	}

	@Override
	public IComponent getParent() {
		return parent;
	}

	@Override
	public IComponent setParent(IComponent parent) {
		this.parent = parent;
		return this;
	}

	@Override
	public int getNumChildren() {
		return children.size();
	}

	@Override
	public List<IComponent> getChildren() {
		return children;
	}

	@Override
	public IComponent setChildren(List<IComponent> value) {
		this.children = value;
		return this;
	}

	@Override
	public IComponent setId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public String getId() {
		if (this.id == null) {
			String id = getAttributeAsString("id");
			if (StringUtil.isEmpty(id))
				id = getLayrContext().getNextId();
			this.id = id;
		}
		return this.id;
	}

	@Override
	public IComponent setLayrContext(RequestContext context) {
		this.layrContext = context;
		return this;
	}

	@Override
	public RequestContext getLayrContext() {
		return this.layrContext;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setRootdir(String rootdir) {
		this.rootdir = rootdir;
	}

	public String getRootdir() {
		return rootdir;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

}
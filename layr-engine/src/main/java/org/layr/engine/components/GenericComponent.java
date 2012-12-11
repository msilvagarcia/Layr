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
package org.layr.engine.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.layr.commons.StringUtil;
import org.layr.engine.IRequestContext;
import org.layr.engine.TemplateParser;
import org.layr.engine.components.template.HolderComponent;
import org.layr.engine.expressions.ComplexExpressionEvaluator;

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

	public static final String CHILDREN = "TemplateParser.CHILDREN/";
	public static final String DOCTYPE_ATTRIBUTE = "GenericComponent.DOCTYPE_ATTRIBUTE";

	private static final String[] styles = new String[] {
		"align","border","background","height","display","min-height","max-height", "width",
		"max-width","min-width","top","left","position","margin","padding","bottom","right"
	};

	private final static String[] events = new String[] {
		"blur", "change", "click",
		"dblclick", "focus", "mousedown", "mousemove",
		"mouseout", "mouseover", "mouseup", "keydown",
		"keypress", "keyup", "select" };

	private List<IComponent> children;
	private Map<String, Object> attributes;
	private IComponent parent;
	private String textContent;
	private String id;
	protected IRequestContext requestContext;

	private String rootdir = "";
	private String extension = "xhtml";
	private String componentName;
	private String qualifiedName;
	private String docTypeDefinition;
	private String snippetName;

	public GenericComponent() {
		super();
		children = new ArrayList<IComponent>();
		attributes = new HashMap<String, Object>();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#configure()
	 */
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
			IComponent compiledTemplate = compile(getTemplate());
			if ( compiledTemplate == null ){
				System.out.println("Cant found: " + getTemplate());
				renderAsComponentStub();
				return;
			}

			renderDocType();
			renderCompiledTemplate(compiledTemplate);
		} catch ( TemplateParsingException e ) {
			throw new IOException(e);
		}
	}

	/**
	 * @throws IOException
	 */
	public void renderDocType() throws IOException {
		if ( StringUtil.isEmpty(docTypeDefinition) )
			return;
		getWriter().append( String.format( docTypeDefinition , getComponentName() ) );
	}

	/**
	 * @param template
	 * @return
	 * @throws TemplateParsingException
	 */
	public IComponent compile(String template) throws TemplateParsingException {
		TemplateParser templateParser = new TemplateParser(getRequestContext());
		IComponent compiledComponent = templateParser.compile(template);
		createHolderComponentToMemorizeCurrentComponentChilden( compiledComponent );
		return compiledComponent;
	}

	/**
	 * @param component
	 * @return
	 */
	public void createHolderComponentToMemorizeCurrentComponentChilden( IComponent component ) {
		if ( component == null )
			return;
		
		HolderComponent holder = new HolderComponent();
		holder.setChildren(getChildren());
		holder.configure();
		getRequestContext().put(CHILDREN + component.getSnippetName(), holder);
	}

	/**
	 * @return
	 */
	public IComponent retrieveMemorizedCurrentChildren(){
		return (IComponent)getRequestContext()
					.get(CHILDREN + getSnippetName());
	}

	/**
	 * Render component as stub.
	 * @throws IOException 
	 */
	public void renderAsComponentStub() throws IOException {
		Writer writer = getWriter();
		getWriter().append('<')
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
	 * @return
	 */
	public Writer getWriter() {
		return requestContext.getWriter();
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
			String componentAttributeReferenceName = getComponentName() + ":" + attribute;
			getRequestContext().put(componentAttributeReferenceName, getParsedAttribute(attribute));
			attributes.add(componentAttributeReferenceName);
		}

		compiledTemplate.render();
		for (String attribute : attributes)
			getRequestContext().put(attribute, null);
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
	public Object clone(IRequestContext context)
			throws CloneNotSupportedException, ServletException, IOException {
		flush();

		List<IComponent> children = new ArrayList<IComponent>();
		GenericComponent clone = (GenericComponent) super.clone();
		clone.setRequestContext(context);

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
		this.setRequestContext(null);
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

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setTextContent(java.lang.String)
	 */
	@Override
	public IComponent setTextContent(String content) {
		this.textContent = content;
		return this;
	}

    /* (non-Javadoc)
     * @see org.layr.engine.components.IComponent#getTextContent()
     */
    @Override
	public String getTextContent() {
		if (textContent == null)
			return null;

		return (String) ComplexExpressionEvaluator.getValue(textContent, getRequestContext(), true);
	}
    
    /**
     * @return
     */
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
		return ComplexExpressionEvaluator.getValue(object.toString(), getRequestContext(), shouldBeEncoded);
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

	/**
	 * @return
	 */
	public Collection<String> getAttributeKeys() {
		return attributes.keySet();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getParent()
	 */
	@Override
	public IComponent getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setParent(org.layr.engine.components.IComponent)
	 */
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
				id = getRequestContext().getNextId();
			this.id = id;
		}
		return this.id;
	}

	@Override
	public IComponent setRequestContext(IRequestContext context) {
		this.requestContext = context;
		return this;
	}

	@Override
	public IRequestContext getRequestContext() {
		return this.requestContext;
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

	@Override
	public String getDocTypeDefinition() {
		return docTypeDefinition;
	}

	@Override
	public void setDocTypeDefinition(String docTypeDefinition) {
		this.docTypeDefinition = docTypeDefinition;
	}

	public String getSnippetName() {
		return snippetName;
	}

	public void setSnippetName(String templateName) {
		this.snippetName = templateName;
	}

}
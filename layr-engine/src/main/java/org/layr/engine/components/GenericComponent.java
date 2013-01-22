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

	private List<IComponent> children;
	private Map<String, Object> attributes;
	private IComponent parent;
	private String textContent;
	private String id;
	protected IRequestContext requestContext;
	private boolean selfCloseable;
	private List<String> ignoredAttributes;

	private String rootdir;
	private String extension;
	private String componentName;
	private String qualifiedName;
	private String docTypeDefinition;
	private String snippetName;

	public GenericComponent() {
		super();
		setChildren(new ArrayList<IComponent>());
		setAttributes(new HashMap<String, Object>());
		setIgnoredAttributes(new ArrayList<String>());
		setSelfCloseable(false);
		setRootdir("");
		setExtension("xhtml");
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#configure()
	 */
	public void configure() throws ServletException, IOException {}

	/* (non-Javadoc)
	 * @see layr.components.IComponent#render()
	 */
	@Override
	public void render() throws IOException {
		try {
			String templateFilePath = measureAndRetrieveComponentTemplateFilePath();
			IComponent compiledTemplate = compile(templateFilePath);
			if ( compiledTemplate == null )
				renderComponentAsStub();
			else
				renderCompiledTemplate(compiledTemplate);
		} catch ( TemplateParsingException e ) {
			throw new IOException(e);
		}
	}

	/**
	 * @throws IOException
	 */
	public void renderComponentAsStub() throws IOException {
		GenericXHtmlRenderer xHtmlRenderer = 
			getXhtmlRenderer().shouldVerifyChildrenNumberToGrantThatComponentIsSelfCloseable();
		xHtmlRenderer.render();
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
	 * Render compiled template.
	 * 
	 * @param compiledTemplate
	 * @throws IOException
	 */
	public void renderCompiledTemplate(IComponent compiledTemplate) throws IOException {
		ArrayList<String> memorizedAttributes = 
				memorizeComponentAttributeThatCouldBePassedThroughComponentParameter();

		compiledTemplate.render();
		for (String attribute : memorizedAttributes)
			forgetComponentAttributeReference(attribute);
	}

	public IRequestContext forgetComponentAttributeReference(String attribute) {
		return getRequestContext().put(attribute, null);
	}

	/**
	 * @return
	 */
	public ArrayList<String> memorizeComponentAttributeThatCouldBePassedThroughComponentParameter() {
		ArrayList<String> attributes = new ArrayList<String>();
		for (String attribute : getAttributeKeys()) {
			String componentAttributeReferenceName = getComponentName() + ":" + attribute;
			getRequestContext().put(componentAttributeReferenceName, getParsedAttribute(attribute));
			attributes.add(componentAttributeReferenceName);
		}

		return attributes;
	}

	/**
	 * @return
	 */
	public String measureAndRetrieveComponentTemplateFilePath() {
		return getRootdir() + "/" + getComponentName() + "." + getExtension();
	}

	/* (non-Javadoc)
	 * @see layr.components.IComponent#clone(layr.RequestContext)
	 */
	@Override
	public Object clone(IRequestContext context)
			throws CloneNotSupportedException, ServletException, IOException {
		flush();

		GenericComponent clone = (GenericComponent) super.clone();
		clone.setRequestContext(context);

		List<IComponent> children = cloneChildren(context, clone);
	
		clone.setChildren(children);
		clone.setTextContent(textContent);
		clone.configure();

		return clone;
	}

	/**
	 * @param requestContext
	 * @param parentComponent
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws ServletException
	 * @throws IOException
	 */
	public List<IComponent> cloneChildren(
				IRequestContext requestContext, GenericComponent parentComponent)
			throws CloneNotSupportedException, ServletException, IOException {
		List<IComponent> children = new ArrayList<IComponent>();

		for (IComponent child : getChildren()) {
			IComponent clonedChild = (IComponent) child.clone(requestContext);
			clonedChild.setParent(parentComponent);
			children.add(clonedChild);
		}

		return children;
	}

	/**
	 * Flushes objects that will be never used after component's rendering.
	 * Custom components should override this method to clean some variables to
	 * avoid memory leaks. Note that you should always call super.flush() to
	 * clean the {@link GenericComponent} internal variables.
	 */
	public void flush() {
		this.setIgnoredAttributes(new ArrayList<String>());
		this.setRequestContext(null);
	}

	@Override
	public void addChild(IComponent child) {
		children.add(child);
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

		return (String) ComplexExpressionEvaluator.getValue(
				textContent, getRequestContext(), true);
	}
    
    /**
     * @return
     */
    public String getNonParsedTextContent() {
    	return textContent;
    }

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String attribute) {
		return attributes.get(attribute);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getParsedAttribute(java.lang.String)
	 */
	public Object getParsedAttribute(String attribute) {
		return getParsedAttribute(attribute, false);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getParsedAttribute(java.lang.String, boolean)
	 */
	public Object getParsedAttribute(String attribute, boolean shouldBeEncoded) {
		Object object = attributes.get(attribute);
		if (object == null)
			return null;
		return ComplexExpressionEvaluator.getValue(object.toString(), getRequestContext(), shouldBeEncoded);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getAttributeAsString(java.lang.String)
	 */
	public String getAttributeAsString(String attr) {
		Object value = getParsedAttribute(attr, true);
		if (value == null)
			return "";

		if ( String.class.isInstance(value) )
			return StringUtil.escape((String)value);

		return value.toString();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getNumChildren()
	 */
	@Override
	public int getNumChildren() {
		return children.size();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getChildren()
	 */
	@Override
	public List<IComponent> getChildren() {
		return children;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setChildren(java.util.List)
	 */
	@Override
	public IComponent setChildren(List<IComponent> value) {
		this.children = value;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setId(java.lang.String)
	 */
	@Override
	public IComponent setId(String id) {
		this.id = id;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getId()
	 */
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

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setRequestContext(org.layr.engine.IRequestContext)
	 */
	@Override
	public IComponent setRequestContext(IRequestContext context) {
		this.requestContext = context;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getRequestContext()
	 */
	@Override
	public IRequestContext getRequestContext() {
		return this.requestContext;
	}

	/**
	 * @param attributes
	 */
	public IComponent setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}

	/**
	 * @return
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setComponentName(java.lang.String)
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	/**
	 * @return
	 */
	public String getComponentName() {
		return componentName;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setRootdir(java.lang.String)
	 */
	public void setRootdir(String rootdir) {
		this.rootdir = rootdir;
	}

	/**
	 * @return
	 */
	public String getRootdir() {
		return rootdir;
	}

	/**
	 * @param extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return
	 */
	public String getExtension() {
		return extension;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getQualifiedName()
	 */
	public String getQualifiedName() {
		return qualifiedName;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setQualifiedName(java.lang.String)
	 */
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getDocTypeDefinition()
	 */
	@Override
	public String getDocTypeDefinition() {
		return docTypeDefinition;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setDocTypeDefinition(java.lang.String)
	 */
	@Override
	public void setDocTypeDefinition(String docTypeDefinition) {
		this.docTypeDefinition = docTypeDefinition;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getSnippetName()
	 */
	public String getSnippetName() {
		return snippetName;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setSnippetName(java.lang.String)
	 */
	public void setSnippetName(String templateName) {
		this.snippetName = templateName;
	}

	/**
	 * @param selfCloseable
	 */
	public void setSelfCloseable(boolean selfCloseable) {
		this.selfCloseable = selfCloseable;
	}

	/**
	 * @return
	 */
	public boolean isSelfCloseable() {
		return selfCloseable;
	}

	/**
	 * @param attribute
	 */
	public void ignoreAttribute(String attribute) {
		ignoredAttributes.add(attribute);
	}
	
	/**
	 * @return
	 */
	public List<String> getIgnoredAttributes() {
		return ignoredAttributes;
	}
	
	/**
	 * @param ignoredAttributes
	 */
	public void setIgnoredAttributes(List<String> ignoredAttributes) {
		this.ignoredAttributes = ignoredAttributes;
	}
	
	/**
	 * @return
	 */
	public GenericXHtmlRenderer getXhtmlRenderer(){
		return new GenericXHtmlRenderer( getWriter(), this );
	}

	/**
	 * @return
	 */
	public boolean hasChildren(){
		return getChildren() != null && getChildren().size() > 0;
	}

	/**
	 * @return
	 */
	public Writer getWriter() {
		return requestContext.getWriter();
	}

}
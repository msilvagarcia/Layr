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
package layr.engine.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import layr.commons.StringUtil;
import layr.engine.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.expressions.Evaluator;


/**
 * Default implementation for components. Software developers can override the
 * methods for a custom component development.
 * 
 * @author Miere Liniel Teixeira
 * @see Component#clone()
 * @see Component#getChildren()
 * @see Component#getId()
 * @see Component#render()
 */
public class GenericComponent implements Component {

	public static final String CHILDREN = GenericComponent.class.getCanonicalName() + ".CHILDREN/";
	public static final String DOCTYPE_ATTRIBUTE = GenericComponent.class.getCanonicalName() + ".DOCTYPE_ATTRIBUTE";

	private List<Component> children;
	private Map<String, Object> attributes;
	private Component parent;
	private String textContent;
	private String id;
	protected RequestContext requestContext;
	private boolean selfCloseable;
	private List<String> ignoredAttributes;

	private String rootdir;
	private String extension;
	private String componentName;
	private String qualifiedName;
	private String docTypeDefinition;
	private String snippetName;

	/**
	 * 
	 */
	public GenericComponent() {
		super();
		setChildren(new ArrayList<Component>());
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
			Component compiledTemplate = compile(templateFilePath);
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
	public Component compile(String template) throws TemplateParsingException {
		TemplateParser templateParser = new TemplateParser(getRequestContext());
		Component compiledComponent = templateParser.compile(template);
		createHolderComponentToMemorizeCurrentComponentChilden( compiledComponent );
		return compiledComponent;
	}

	/**
	 * @param component
	 * @return
	 */
	public void createHolderComponentToMemorizeCurrentComponentChilden( Component component ) {
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
	public Component retrieveMemorizedCurrentChildren(){
		return (Component)getRequestContext()
					.get(CHILDREN + getSnippetName());
	}

	/**
	 * Render compiled template.
	 * 
	 * @param compiledTemplate
	 * @throws IOException
	 */
	public void renderCompiledTemplate(Component compiledTemplate) throws IOException {
		ArrayList<String> memorizedAttributes = 
				memorizeComponentAttributeThatCouldBePassedThroughComponentParameter();

		compiledTemplate.render();
		for (String attribute : memorizedAttributes)
			forgetComponentAttributeReference(attribute);
	}

	public void forgetComponentAttributeReference(String attribute) {
		getRequestContext().put(attribute, null);
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
	public Object clone(RequestContext context)
			throws CloneNotSupportedException, ServletException, IOException {
		flush();

		GenericComponent clone = (GenericComponent) super.clone();
		clone.setRequestContext(context);

		List<Component> children = cloneChildren(context, clone);
	
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
	public List<Component> cloneChildren(
				RequestContext requestContext, GenericComponent parentComponent)
			throws CloneNotSupportedException, ServletException, IOException {
		List<Component> children = new ArrayList<Component>();

		for (Component child : getChildren()) {
			Component clonedChild = (Component) child.clone(requestContext);
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
	public void addChild(Component child) {
		children.add(child);
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setTextContent(java.lang.String)
	 */
	@Override
	public Component setTextContent(String content) {
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
		return new Evaluator( requestContext, textContent ).eval();
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
		Object object = getAttribute(attribute);
		if ( object == null )
			return null;
		if ( !String.class.isInstance( object ) )
			return object;
		Evaluator evaluator = new Evaluator( requestContext, (String)object );
		return evaluator.parse();
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getAttributeAsString(java.lang.String)
	 */
	public String getAttributeAsString(String attr) {
		Object value = getParsedAttribute(attr);
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
	public Component setAttribute(String attribute, Object value) {
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
	public Component getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setParent(org.layr.engine.components.IComponent)
	 */
	@Override
	public Component setParent(Component parent) {
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
	public List<Component> getChildren() {
		return children;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setChildren(java.util.List)
	 */
	@Override
	public Component setChildren(List<Component> value) {
		this.children = value;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setId(java.lang.String)
	 */
	@Override
	public Component setId(String id) {
		this.id = id;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getId()
	 */
	@Override
	public String getId() {
		if (this.id == null)
			this.id = getAttributeAsString("id");
		return this.id;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#setRequestContext(org.layr.engine.IRequestContext)
	 */
	@Override
	public Component setRequestContext(RequestContext context) {
		this.requestContext = context;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.components.IComponent#getRequestContext()
	 */
	@Override
	public RequestContext getRequestContext() {
		return this.requestContext;
	}

	/**
	 * @param attributes
	 */
	public Component setAttributes(Map<String, Object> attributes) {
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
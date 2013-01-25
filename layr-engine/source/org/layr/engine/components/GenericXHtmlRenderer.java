package org.layr.engine.components;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.layr.commons.StringUtil;

public class GenericXHtmlRenderer {
	
	GenericComponent snippet;
	Writer writer;
	boolean shouldUseQualifiedNameAsTagName;
	boolean shouldVerifyChildrenNumberToGrantThatComponentIsSelfCloseable;

	public GenericXHtmlRenderer(Writer writer, GenericComponent snippet) {
		this.snippet = snippet;
		this.writer = writer;
		this.shouldUseQualifiedNameAsTagName = true;
		this.shouldVerifyChildrenNumberToGrantThatComponentIsSelfCloseable = false;
	}

	/**
	 * @throws IOException
	 */
	public void render() throws IOException {
		renderDocType();
		renderOpenningTag();

		for (String attributeName : getAttributeNames()) {
			if (shouldIgnoreAttribute(attributeName))
				continue;

			String attributeValue = getAttributeValue(attributeName);
			if (!StringUtil.isEmpty(attributeValue))
				renderAttribute(attributeName, attributeValue);
		}

		renderChilrenAndCloseTag();
	}

	/**
	 * @throws IOException
	 */
	public void renderDocType() throws IOException {
		String docTypeDefinition = snippet.getDocTypeDefinition();
		if ( StringUtil.isEmpty(docTypeDefinition) )
			return;

		String doctype = String.format(
				docTypeDefinition, snippet.getComponentName() );
		writer.append( doctype );
	}

	/**
	 * @throws IOException
	 */
	public void renderOpenningTag() throws IOException {
		writer.append("<").append(getComponentName());
	}

	/**
	 * @return
	 */
	public String getComponentName() {
		if ( shouldUseQualifiedNameAsTagName )
			return snippet.getQualifiedName();
		return snippet.getComponentName();
	}

	/**
	 * @return
	 */
	public Set<String> getAttributeNames() {
		return snippet.getAttributes().keySet();
	}

	/**
	 * @param attr
	 * @return
	 */
	public boolean shouldIgnoreAttribute(String attr) {
		return snippet.getIgnoredAttributes().contains(attr);
	}

	/**
	 * @param attributeName
	 * @return
	 */
	public String getAttributeValue(String attributeName) {
		return snippet.getAttributeAsString(attributeName);
	}

	/**
	 * @param writer
	 * @param attributeName
	 * @param attributeValue
	 * @throws IOException
	 */
	public void renderAttribute( String attributeName, String attributeValue )
			throws IOException {
		writer.append(' ')
			  .append(attributeName)
			  .append("=\"")
			  .append(attributeValue)
			  .append("\"");
	}

	/**
	 * @throws IOException
	 */
	public void renderChilrenAndCloseTag() throws IOException {
		if (isSnippetSelfCloseable()) {
			writer.append(" />");
			return;
		}
		
		writer.append('>');
		renderChildren();
		writer.append("</")
			  .append(getComponentName())
			  .append('>');
	}

	/**
	 * @return
	 */
	public boolean isSnippetSelfCloseable(){
		return snippet.isSelfCloseable()
			|| ( shouldVerifyChildrenNumberToGrantThatComponentIsSelfCloseable && !snippet.hasChildren() );
	}

	/**
	 * @throws IOException
	 */
	public void renderChildren() throws IOException {
		for (IComponent child : snippet.getChildren()) {
			child.setRequestContext(snippet.getRequestContext());
			child.render();
		}
	}

	/**
	 * @return
	 */
	public GenericXHtmlRenderer shouldNotUseQualifiedNameAsTagNameButComponentName(){
		shouldUseQualifiedNameAsTagName = false;
		return this;
	}
	
	/**
	 * @return
	 */
	public GenericXHtmlRenderer shouldVerifyChildrenNumberToGrantThatComponentIsSelfCloseable(){
		shouldVerifyChildrenNumberToGrantThatComponentIsSelfCloseable = true;
		return this;
	}
}

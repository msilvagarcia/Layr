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
package layr.api;

import java.io.IOException;
import java.util.List;

/**
 * Default component interface. Here are defined the minimum methods to the
 * component works well on Layr Life Cycle.
 */
public interface Component extends Cloneable {

	/**
	 * Renders the component into a HTML document.
	 * 
	 * @throws IOException
	 */
	public void render() throws IOException;

	/**
	 * Clones the component.
	 * 
	 * Once the resource template is parsed, the component Stack Tree is
	 * generated. By default, if a template was already parsed the Layr Life
	 * Cycle doesn't parses it again, it clones the object to improve the parse
	 * performance.<br/>
	 * <br/>
	 * Note: this method is handled by the Layr Life Cycle and should not be
	 * called unless you really knows what are you doing.
	 * 
	 * @param layrContext
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ServletException
	 */
	public Object clone(RequestContext context)
			throws CloneNotSupportedException, IOException;

	/**
	 * Appends a child at end of children's list.
	 * 
	 * @param child
	 */
	public void addChild(Component child);

	/**
	 * Retrieves the number of children,
	 * 
	 * @return
	 */
	public int getNumChildren();

	/**
	 * Set a new list of component to use as children of the component.
	 * 
	 * @param value
	 */
	public Component setChildren(List<Component> value);

	/**
	 * Retrieves the children's list.
	 * 
	 * @return
	 */
	public List<Component> getChildren();

	/**
	 * Set text content of the component. Some components are designed to handle
	 * the tag's markup <i>default value</i> (<code><i>&lt;ns:component&gt;Here
	 * goes the default value&lt;/ns:component&gt;</i></code>). By default this
	 * method should store the <i>default value</i> into a String field inside
	 * the component.
	 * 
	 * @param content
	 * @return
	 */
	public Component setTextContent(String content);

	/**
	 * Retrieves the text content of the component.
	 * 
	 * @return the text content string.
	 * @see Component#setTextContent(String)
	 */
	public String getTextContent();

	/**
	 * Retrieves an attribute value from the component.
	 * 
	 * @param attribute
	 * @return
	 */
	public Object getAttribute(String attribute);

	/**
	 * Set's a value to the component attribute.
	 * 
	 * @param attribute
	 * @param value
	 */
	public Component setAttribute(String attribute, Object value);

	/**
	 * Retrieves the parent component.
	 * 
	 * @return
	 */
	public Component getParent();

	/**
	 * Set's the parent component.
	 * 
	 * @param parent
	 */
	public Component setParent(Component parent);

	/**
	 * Get the component's unique identification. By default, the main
	 * implementation of component generates a sequential id's for null
	 * component's id at the render time and is usually stored at <i>'id'</i>
	 * HTML attribute.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Set the component's unique identification.
	 * 
	 * @param id
	 */
	public Component setId(String id);

	/**
	 * Sets the current LayrContext.
	 */
	public Component setRequestContext(RequestContext context);

	/**
	 * Retrieves the current LayrContext
	 */
	public RequestContext getRequestContext();

	/**
	 * Defines the component name.
	 * 
	 * @param componentName
	 */
	void setComponentName(String componentName);

	/**
	 * Java Components are designed to have a template related to. This method
	 * sets where is the directory which the template is placed in.
	 * @param rootDir
	 */
	public void setRootdir(String rootDir);

	/**
	 * A method designed to configure the main component behavior.
	 * It is executed at every request.
	 * @throws IOException 
	 * @throws ServletException 
	 */
	void configure() throws IOException;

	Object getParsedAttribute(String attribute);

	String getAttributeAsString(String attribute);

	void setQualifiedName(String qualifiedName);

	String getQualifiedName();
	
	String getDocTypeDefinition();
	
	void setDocTypeDefinition( String docType );

	String getSnippetName();

	void setSnippetName(String templateName);
}

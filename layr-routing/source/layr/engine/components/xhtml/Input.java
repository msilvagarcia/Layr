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
package layr.engine.components.xhtml;

import java.io.IOException;

import layr.commons.StringUtil;
import layr.engine.expressions.Evaluator;

public class Input extends XHtmlComponent {
	
	String[] autoBindableInputTypes = new String[] {
			"text","file","passoword","button","hidden",
			// Some HTML5 input types
			"color","date","datetime","datetime-local", "email",
			"month","number","range","search","tel","time","url","week"
		};
	
	private String type;
	private String checked;
	private boolean checkedCanBeNull = false;
	private Object originalCheckedAttribute;
	private Object originalValueAttribute;

	private Object value;

	@Override
	public void configure() throws IOException {
		setComponentName("input");
		setSelfCloseable(true);
		saveOriginalAttributeValues();
		super.configure();
	}

	public void saveOriginalAttributeValues() {
		if ( originalCheckedAttribute == null )
			originalCheckedAttribute = getAttribute("checked");
		else
			setAttribute( "checked", null );

		if ( originalValueAttribute == null )
			originalValueAttribute = getAttribute("value");
		else
			setAttribute("value", null);
	}

	@Override
	public void render() throws IOException {
		type = getType();
		value = parseExpression(originalValueAttribute);

		String name = getAttributeAsString("name");
		configureTextInputValueAttribute(name);
		configureDefaultCheckboxValueAttribute();
		configureRadioAndCheckbox(name, value);

		super.render();
	}

	public void configureTextInputValueAttribute(String name) {
		if ( isTextInput()
		&&   !StringUtil.isEmpty(name)
		&&   originalValueAttribute == null )
				setAttribute("value", "#{"+name+"}");
	}

	public void configureDefaultCheckboxValueAttribute() {
		if (type.equals("checkbox") && originalValueAttribute == null ) {
			setAttribute("value","true");
			value = "true";
		}
	}

	public void configureRadioAndCheckbox(String name, Object value) {
		if ( !type.equals("radio") && !type.equals("checkbox"))
			return;

		Object checked = getChecked();
		if  ( (checked == null && value != null 
		&&     value.equals( parseExpression("#{"+name+"}") ))
		||  ( checked != null && (checked.equals(true) || checked.equals("true")) ) )
			setAttribute("checked","checked");
		else
			setAttribute("checked",checked);
	}

	public Object parseExpression( Object expression ){
		if ( expression == null )
			return null;
		return new Evaluator(getRequestContext(), expression.toString()).eval();
	}

	public boolean isTextInput () {
		for ( String validType : autoBindableInputTypes )
			if ( validType.equals(type) )
				return true;
		return false;
	}

	public String getChecked() {
		if ( checked == null && !checkedCanBeNull ){
			Object parsedAttribute = parseExpression( originalCheckedAttribute );
			if ( parsedAttribute != null )
				checked = Boolean.class.isInstance(parsedAttribute)
					? ((Boolean)parsedAttribute).toString()
					:  (String) parsedAttribute;
		}
		checkedCanBeNull = true;
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getType() {
		if ( type == null ) {
			type = getAttributeAsString("type");
			if ( StringUtil.isEmpty(type) )
				type = "text";
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

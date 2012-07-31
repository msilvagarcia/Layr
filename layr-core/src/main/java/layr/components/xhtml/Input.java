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
package layr.components.xhtml;

import java.io.IOException;

import javax.servlet.ServletException;

import layr.binding.ComplexExpressionEvaluator;
import layr.util.StringUtil;


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

	@Override
	public void configure() throws ServletException, IOException {
		setComponentName("input");
		setSelfCloseable(true);

		type = getType();

		String name = getAttributeAsString("name");
		if (StringUtil.isEmpty(name)) {
			setAttribute("name", getId());
			name = getId();
		}

		if ( isTextInput() ) {
			if ( name != null && getAttribute("value") == null )
				setAttribute("value", "#{"+name+"}");
			return;
		}

		Object value = getParsedAttribute("value");
		Object checked = getChecked();

		if (type.equals("checkbox") && value == null ) {
			setAttribute("value","true");
			value = "true";
		}

		if (type.equals("radio") || type.equals("checkbox")) {
			if  ( (checked == null && value != null 
						&& value.equals(ComplexExpressionEvaluator.getValue("#{"+name+"}", layrContext, true)) )
			||  ( checked != null && (checked.equals(true) || checked.equals("true")) ) )
				setAttribute("checked","checked");
			else
				setAttribute("checked",null);
		}

		super.configure();
	}
	
	public boolean isTextInput () {
		for ( String validType : autoBindableInputTypes )
			if ( validType.equals(type) )
				return true;

		return false;
	}

	public String getChecked() {
		if ( checked == null && !checkedCanBeNull )
			checked = (String) getParsedAttribute("checked");
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

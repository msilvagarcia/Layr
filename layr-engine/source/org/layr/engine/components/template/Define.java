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
package org.layr.engine.components.template;

import java.io.IOException;

import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.HolderComponent;
import org.layr.engine.expressions.Evaluator;

public class Define extends GenericComponent {

	@Override
	public void configure() {
		String name = getAttributeAsString("name");
		requestContext.put(Var.LAYR_COMPONENTS_TEMPLATE_VALUE + name, getDefinedValue());
	}

	@Override
	public void render() throws IOException {}
	
	public Object getDefinedValue() {
		if (getNumChildren() == 0) {
			String value = getAttributeAsString("value");
			return new Evaluator(requestContext, value).eval();
		}

		HolderComponent component = new HolderComponent();
		component.setRequestContext(getRequestContext());
		component.setChildren(getChildren());
		return component;
	}

}

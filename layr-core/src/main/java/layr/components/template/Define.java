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
package layr.components.template;

import java.io.IOException;

import layr.binding.ComplexExpressionEvaluator;
import layr.components.GenericComponent;


public class Define extends GenericComponent {

	@Override
	public void configure() {
		String name = getAttributeAsString("name");
		layrContext.put(Var.COFFEE_COMPONENTS_TEMPLATE_VALUE + name, getDefinedValue());
	}

	@Override
	public void render() throws IOException {}
	
	public Object getDefinedValue() {
		if (getNumChildren() == 0)
			return ComplexExpressionEvaluator.getValue(getAttributeAsString("value"), layrContext);

		HolderComponent component = new HolderComponent();
		component.setLayrContext(getLayrContext());
		component.setChildren(getChildren());
		return component;
	}

}

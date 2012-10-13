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
package layr.core.components.template;

import java.io.IOException;
import java.io.PrintWriter;

import layr.core.components.GenericComponent;
import layr.core.components.IComponent;
import layr.core.expressions.ComplexExpressionEvaluator;


public class Var extends GenericComponent {
	
	public static final String LAYR_COMPONENTS_TEMPLATE_VALUE = "LAYR_COMPONENTS_TEMPLATE_VALUE.";

	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		String name = getAttributeAsString("name");
		Object definedValue = getLayrContext().get(LAYR_COMPONENTS_TEMPLATE_VALUE + name);
		
		if (definedValue == null)
			return;

		if (IComponent.class.isInstance(definedValue)) {
			((IComponent)definedValue).render();
			return;
		}

		PrintWriter writer = layrContext.getResponse().getWriter();
		Object value = ComplexExpressionEvaluator.getValue(definedValue.toString(), getLayrContext());

		if (value != null)
			writer.append(value.toString());
	}

}

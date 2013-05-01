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
package layr.engine.components.template;

import java.io.IOException;
import java.io.Writer;

import layr.engine.components.Component;
import layr.engine.components.GenericComponent;
import layr.engine.expressions.Evaluator;


public class Var extends GenericComponent {

	public static final String LAYR_COMPONENTS_TEMPLATE_VALUE = "LAYR_COMPONENTS_TEMPLATE_VALUE.";

	@Override
	public void configure() {
	}

	@Override
	public void render() throws IOException {
		String name = getAttributeAsString( "name" );
		Object definedValue = getRequestContext().get( LAYR_COMPONENTS_TEMPLATE_VALUE + name );

		if (definedValue == null)
			return;

		if (Component.class.isInstance( definedValue )) {
			((Component) definedValue).render();
			return;
		}

		renderValue( definedValue );
	}

	public void renderValue(Object definedValue) throws IOException {
		Writer writer = requestContext.getWriter();
		Object value = new Evaluator( getRequestContext(), definedValue.toString() ).eval();

		if (value != null)
			writer.append( value.toString() );
	}

}

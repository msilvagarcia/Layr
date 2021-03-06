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

import static layr.commons.StringUtil.*;
import layr.api.Component;
import layr.engine.components.GenericComponent;
import layr.engine.components.TemplateParsingException;


public class Template extends GenericComponent {
	
	@Override
	public void configure() {}

	@Override
	public void render() throws IOException {
		String src = getAttributeAsString("src");
		if (!isEmpty( src ) )
			renderIncludedSource( src );
		Object when = getParsedAttribute("when");
		if ( when != null )
			tryRenderChildren( when );
	}

	public void renderIncludedSource(String src) throws IOException {
		try {
			Component template = compile(src);
            if ( template == null )
    			throw new IOException("Can't find template '" + src + "'.");
			template.render();
		} catch (TemplateParsingException e) {
			throw new IOException(e);
		}
	}

	public void tryRenderChildren(Object when) throws IOException {
		Object equalsAttribute = getEqualsAttribute();
		if ( when.equals( equalsAttribute ) )
			renderChildren();
	}

	public Object getEqualsAttribute() {
		Object equals = getParsedAttribute("equals");
		if ( equals == null )
			equals = true;
		return equals;
	}

	public void renderChildren() throws IOException {
		for ( Component component : getChildren() )
			component.render();
	}
}

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
package org.layr.engine.components.xhtml;

import java.io.IOException;

import org.layr.engine.components.GenericComponent;
import org.layr.engine.components.GenericXHtmlRenderer;

/**
 * Default implementation for XHTML Components.
 */
public class XHtmlComponent extends GenericComponent {

	/* (non-Javadoc)
	 * @see org.layr.engine.components.GenericComponent#render()
	 */
	@Override
	public void render() throws IOException {
		GenericXHtmlRenderer xHtmlRenderer = getXhtmlRenderer()
			.shouldNotUseQualifiedNameAsTagNameButComponentName();
		xHtmlRenderer.render();
	}
}

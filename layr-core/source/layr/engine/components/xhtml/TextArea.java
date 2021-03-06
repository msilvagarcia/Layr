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
import layr.engine.components.TextNode;

public class TextArea extends XHtmlComponent {

	@Override
	public void configure() throws IOException {
		setComponentName("textarea");
		super.configure();
	}

	@Override
	public void render() throws IOException {
		String name = getAttributeAsString("name");
		if (StringUtil.isEmpty(name))
			setAttribute("name", getId());

		if (!hasChildren())
			addChild(new TextNode("#{" + name + "}"));

		super.render();
	}
}

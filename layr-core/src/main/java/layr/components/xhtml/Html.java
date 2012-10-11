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
package layr.components.xhtml;

import java.io.IOException;
import java.io.PrintWriter;

import layr.commons.StringUtil;

public class Html extends XHtmlComponent {

	public static final String DOCTYPE_ATTRIBUTE = "DOCTYPE_ATTRIBUTE";

	@Override
	public void configure() {
		setComponentName("html");
		ignoreAttribute(DOCTYPE_ATTRIBUTE);

		if (getAttribute("xmlns") == null)
			setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
	}

	@Override
	public void render() throws IOException {
		String doctype = (String) getAttribute(DOCTYPE_ATTRIBUTE);

		PrintWriter writer = getLayrContext().getResponse().getWriter();
		if (!StringUtil.isEmpty(doctype)) {
			writer.append(doctype);
		} else {
			writer.append("<!DOCTYPE html>");
		}

		super.render();
	}

}

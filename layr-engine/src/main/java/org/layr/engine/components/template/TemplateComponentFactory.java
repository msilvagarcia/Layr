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
package org.layr.engine.components.template;

import org.layr.engine.components.DefaultComponentFactory;
import org.layr.engine.components.TagLib;

/**
 * Template components' factory.
 * 
 * @since Layr 1.0
 */
@TagLib("urn:layr:template")
public class TemplateComponentFactory extends DefaultComponentFactory {

	@Override
	public void configure() {
		register("define", Define.class);
		register("foreach", Foreach.class);
		register("include", Template.class);
		register("var", Var.class);
		register("template", Template.class);
		register("component", HolderComponent.class);
        register("children", Children.class);
	}

}

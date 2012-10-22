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
package layr.core.components;

import layr.core.RequestContext;
import layr.core.TemplateParser;
import layr.core.components.template.TemplateComponentFactory;

public interface IComponentFactory {

/**
 * Creates a new component.<br/>
 * <br/>
 * The default implementation is defined by {@link AbstractComponentFactory} and it's called
 * by the default component parser {@link TemplateParser}. The parser expects that new component
 * has the layrContext set and configured.<br/>
 * <br/>
 * Note that you should not implement this interface by yourself unless you really knows what are
 * you doing. Otherwise, choose to extends {@link AbstractComponentFactory} or {@link TemplateComponentFactory}
 * classes.
 * 
 * @param name
 * @param layrContext
 * @return
 * @throws InstantiationException
 * @throws IllegalAccessException
 * @see IComponent#setLayrContext(RequestContext)
 * @see IComponent#configure()
 */
	public IComponent newComponent(String name, String qName, RequestContext context) throws InstantiationException, IllegalAccessException;

}
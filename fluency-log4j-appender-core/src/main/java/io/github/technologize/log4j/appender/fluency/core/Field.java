/**
 * Copyright [2021] [Bharat Gadde]
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
package io.github.technologize.log4j.appender.fluency.core;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = "Field", category = Node.CATEGORY, elementType = Field.ELEMENT_TYPE, printObject = true)
public class Field {
	
	public static final String ELEMENT_TYPE = "field";
	
	private static final Logger LOGGER = StatusLogger.getLogger();

    private final String name;
    private final String value;

    protected Field(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

	public static Logger getLogger() {
		return LOGGER;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@PluginFactory
    public static Field createStaticField(
            @PluginAttribute("name") final String name,
            @PluginValue("value") final String value) {
        if (name == null || name.isEmpty() ) {
            LOGGER.error("Property name cannot be null");
        }
        return new Field(name, value);
    }

}

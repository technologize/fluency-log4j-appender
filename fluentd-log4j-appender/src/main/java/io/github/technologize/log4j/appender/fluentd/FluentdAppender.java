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
package io.github.technologize.log4j.appender.fluentd;

import java.io.Serializable;
import java.util.Objects;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import io.github.technologize.log4j.appender.fluency.core.Field;
import io.github.technologize.log4j.appender.fluency.core.FluencyAppender;
import io.github.technologize.log4j.appender.fluency.core.FluencyConfig;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = FluentdAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class FluentdAppender extends FluencyAppender {

	public static final String PLUGIN_NAME = "Fluentd";

	protected FluentdAppender(String name, String tag, Field[] fields, FluencyConfig fluentdConfig, Filter filter,
			Layout<? extends Serializable> layout, String ignoreExceptions) {
		super(name, tag, fields, fluentdConfig, filter, layout, ignoreExceptions);
	}

	@PluginFactory
	public static FluentdAppender createAppender(@PluginAttribute("name") final String name,
			@PluginAttribute("tag") @Required(message = "tag is required") final String tag,
			@PluginAttribute("ignoreExceptions") final String ignoreExceptions,
			@PluginElement(Field.ELEMENT_TYPE) final Field[] fields,
			@PluginElement(FluencyConfig.ELEMENT_TYPE) final FluentdConfig fluentdConfig,
			@PluginElement(Layout.ELEMENT_TYPE) Layout<? extends Serializable> layout,
			@PluginElement(Filter.ELEMENT_TYPE) final Filter filter) {

		FluencyConfig config = Objects.requireNonNullElse(fluentdConfig, new FluentdConfig());
		return new FluentdAppender(name, tag, fields, config, filter, layout, ignoreExceptions);
	}

}

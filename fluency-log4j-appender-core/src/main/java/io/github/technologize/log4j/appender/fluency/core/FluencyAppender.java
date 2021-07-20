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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.NameAbbreviator;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.datetime.FixedDateFormat;
import org.apache.logging.log4j.core.util.datetime.FixedDateFormat.FixedFormat;
import org.apache.logging.log4j.status.StatusLogger;
import org.komamitsu.fluency.EventTime;
import org.komamitsu.fluency.Fluency;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = FluencyAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class FluencyAppender extends AbstractAppender {

	public static final String PLUGIN_NAME = "Fluency";

	private static final StatusLogger LOGGER = StatusLogger.getLogger();
	
	private static final NameAbbreviator abbreviator = NameAbbreviator.getAbbreviator("1.");
	private static final FixedDateFormat timeStampFormatter = FixedDateFormat.create(FixedFormat.DEFAULT_NANOS);

	private final Fluency fluency;
	private final String tag;
	private final Map<String, PatternLayout> fieldsParams;
	

	private FluencyAppender(final String name, final String tag, final Field[] fields,
			final FluencyConfig fluencyConfig, final Filter filter, final Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
		
		this.tag = tag;
		this.fieldsParams = new HashMap<>();

		for (Field field : fields) {
			/* No Need to write values if key or value is blank */
			if (field.getName() != null && field.getValue() != null && !field.getName().isEmpty()
					&& !field.getValue().isEmpty()) {
				PatternLayout valueLayout = PatternLayout.newBuilder().withPattern(field.getValue()).build();
				this.fieldsParams.put(field.getName(), valueLayout);
			}
		}
		this.fluency = fluencyConfig.makeFluency();
	}

	@PluginFactory
	public static FluencyAppender createAppender(@PluginAttribute("name") final String name,
			@PluginAttribute("tag") @Required(message = "tag is required") final String tag,
			@PluginAttribute("ignoreExceptions") final String ignore,
			@PluginElement(Field.ELEMENT_TYPE) final Field[] fields,
			@PluginElement(FluencyConfig.ELEMENT_TYPE) final FluencyConfig fluencyConfig,
			@PluginElement(Layout.ELEMENT_TYPE) Layout<? extends Serializable> layout,
			@PluginElement(Filter.ELEMENT_TYPE) final Filter filter) {
		
		final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

		if (tag == null || tag.isEmpty()) {
			throw new IllegalArgumentException("tag is required");
		}

		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		return new FluencyAppender(name, tag, fields, fluencyConfig, filter, layout, ignoreExceptions);
	}

	@Override
	public void append(LogEvent logEvent) {
		if (this.fluency == null) {
			return;
		}
		
		String level = logEvent.getLevel().name();
        String loggerName = logEvent.getLoggerName();
        String message = new String(this.getLayout().toByteArray(logEvent));
        
        Map<String, Object> m = new HashMap<>();
        m.put("level", level);

        StackTraceElement logSource = logEvent.getSource();       
        
        if (logSource == null || logSource.getFileName() == null) {
            m.put("sourceFile", "<unknown>");
        } else {
            m.put("sourceFile", logSource.getFileName());
        }

        if (logSource == null || logSource.getClassName() == null) {
            m.put("sourceClass", "<unknown>");
        } else {
            m.put("sourceClass", logEvent.getSource().getClassName());
        }

        if (logSource == null || logSource.getMethodName() == null) {
            m.put("sourceMethod", "<unknown>");
        } else {
            m.put("sourceMethod", logEvent.getSource().getMethodName());
        }

        if (logSource == null || logSource.getLineNumber() == 0) {
            m.put("sourceLine", 0);
        } else {
            m.put("sourceLine", logEvent.getSource().getLineNumber());
        }

        StringBuilder loggerNameBuilder = new StringBuilder();
    	abbreviator.abbreviate(loggerName, loggerNameBuilder);
        m.put("logger", loggerNameBuilder.toString());
        
        m.put("loggerFull", loggerName);
        m.put("message", message);
        m.put("thread", logEvent.getThreadName());
        
        for (Entry<String, PatternLayout> fieldParam : this.fieldsParams.entrySet()) {
			m.put(fieldParam.getKey(), fieldParam.getValue().toSerializable(logEvent));
		}

        m.put("@timestamp", timeStampFormatter.formatInstant(logEvent.getInstant()));
        
        try {
        	EventTime eventTime = EventTime.fromEpoch(logEvent.getInstant().getEpochMillisecond(), logEvent.getInstant().getNanoOfMillisecond());
            this.fluency.emit(this.tag, eventTime, m);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
	}
}

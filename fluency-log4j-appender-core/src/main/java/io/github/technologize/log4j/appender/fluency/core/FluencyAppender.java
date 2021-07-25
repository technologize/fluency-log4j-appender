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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.NameAbbreviator;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.status.StatusLogger;
import org.komamitsu.fluency.EventTime;
import org.komamitsu.fluency.Fluency;

/**
 * @author Bharat Gadde
 *
 */
public class FluencyAppender extends AbstractAppender {

	private static final StatusLogger LOGGER = StatusLogger.getLogger();
	private static final String UNKNOWN = "<unknown>";
	
	private static final NameAbbreviator abbreviator = NameAbbreviator.getAbbreviator("1.");

	private final Fluency fluency;
	private final String tag;
	private final Map<String, PatternLayout> fieldsParams;
	

	protected FluencyAppender(final String name, final String tag, final Field[] fields,
			final FluencyConfig fluencyConfig, final Filter filter, final Layout<? extends Serializable> layout,
			final String ignoreExceptions) {
		
		super(name, filter, Objects.requireNonNullElse(layout, PatternLayout.createDefaultLayout()),
				Booleans.parseBoolean(ignoreExceptions, true), Property.EMPTY_ARRAY);
		
		/* Tag cannot be Empty */
		Assert.requireNonEmpty(tag, "tag is required");
		
		this.tag = tag;
		this.fieldsParams = new HashMap<>();

		for (Field field : fields) {
			/* No Need to write values if key or value is blank */
			if (Assert.isNonEmpty(field.getPattern()) && Assert.isNonEmpty(field.getName())) {				
				PatternLayout valueLayout = PatternLayout.newBuilder().withPattern(field.getPattern()).build();
				this.fieldsParams.put(field.getName(), valueLayout);
			}
		}
		Assert.requireNonEmpty(fluencyConfig, "Config is required");

		this.fluency = fluencyConfig.makeFluency();
	}

	@Override
	public void append(LogEvent logEvent) {
		if (this.fluency == null) {
			return;
		}
		
		String level = logEvent.getLevel().name();
        String loggerName = logEvent.getLoggerName();
        String message = new String(this.getLayout().toByteArray(logEvent));
        
        Map<String, Object> logEventData = new HashMap<>();
        logEventData.put("level", level);

        StackTraceElement logSource = logEvent.getSource();       
        
        
        
        if (Assert.isEmpty(logSource)) {
        	logEventData.put("sourceFile", UNKNOWN);
        	logEventData.put("sourceClass", UNKNOWN);
        	logEventData.put("sourceMethod", UNKNOWN);
        	logEventData.put("sourceLine", 0);
		} else {
			logEventData.put("sourceFile", Objects.requireNonNullElse(logSource.getFileName(), UNKNOWN));
			logEventData.put("sourceClass", Objects.requireNonNullElse(logSource.getClassName(), UNKNOWN));
			logEventData.put("sourceMethod", Objects.requireNonNullElse(logSource.getMethodName(), UNKNOWN));
			logEventData.put("sourceLine", Objects.requireNonNullElse(logSource.getLineNumber(), 0));
		}

        StringBuilder loggerNameBuilder = new StringBuilder();
    	abbreviator.abbreviate(loggerName, loggerNameBuilder);
        logEventData.put("logger", loggerNameBuilder.toString());
        
        logEventData.put("loggerFull", loggerName);
        logEventData.put("message", message);
        logEventData.put("thread", logEvent.getThreadName());
        
        for (Entry<String, PatternLayout> fieldParam : this.fieldsParams.entrySet()) {
			logEventData.put(fieldParam.getKey(), fieldParam.getValue().toSerializable(logEvent));
		}
        
		/*
		 * Refer: https://www.elastic.co/guide/en/elasticsearch/reference/current/date.html
		 * Format that supports nanos is strict_date_optional_time_nanos which is same as DateTimeFormatter.ISO_INSTANT
		 */
		logEventData.put("@timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant
				.ofEpochSecond(logEvent.getInstant().getEpochSecond(), logEvent.getInstant().getNanoOfSecond())));
                
        try {
			EventTime eventTime = EventTime.fromEpoch(logEvent.getInstant().getEpochSecond(),
					logEvent.getInstant().getNanoOfSecond());
            this.fluency.emit(this.tag, eventTime, logEventData);
        } catch (IOException e) {
            LOGGER.error("Error while emitting message: {}", e.getMessage());
        }
	}
}

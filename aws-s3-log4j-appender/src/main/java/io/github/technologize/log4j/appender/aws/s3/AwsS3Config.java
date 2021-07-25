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
package io.github.technologize.log4j.appender.aws.s3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.logging.log4j.status.StatusLogger;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.aws.s3.FluencyBuilderForAwsS3;
import org.komamitsu.fluency.aws.s3.FluencyBuilderForAwsS3.FormatType;
import org.komamitsu.fluency.aws.s3.ingester.S3DestinationDecider;

import io.github.technologize.log4j.appender.fluency.core.FluencyConfig;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = AwsS3Config.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = FluencyConfig.ELEMENT_TYPE, printObject = true)
public class AwsS3Config implements FluencyConfig {
	
	/**
	 * Config plugin Name
	 */
	public static final String PLUGIN_NAME = "AwsS3Config";
	
	private static final StatusLogger LOGGER = StatusLogger.getLogger();
	
    /**
     * builder for fluency
     */
    private FluencyBuilderForAwsS3 fluencyBuilder;    
    
    /**
     * Creates config depending on values given in configurationFile of log4j
     * 
     * @param formatType
     * @param formatCsvColumnNames
     * @param awsEndpoint
     * @param awsRegion
     * @param awsAccessKeyId
     * @param awsSecretAccessKey
     * @param senderRetryMax
     * @param senderRetryIntervalMillis
     * @param senderMaxRetryIntervalMillis
     * @param senderRetryFactor
     * @param senderWorkBufSize
     * @param compressionEnabled
     * @param s3KeyPrefix
     * @param s3KeySuffix
     * @param zoneOffsetId
     * @param maxBufferSize
     * @param bufferChunkInitialSize
     * @param bufferChunkRetentionSize
     * @param bufferChunkRetentionTimeMillis
     * @param flushAttemptIntervalMillis
     * @param fileBackupDir
     * @param waitUntilBufferFlushed
     * @param waitUntilFlusherTerminated
     * @param jvmHeapBufferMode
     * @param customS3DestinationDecider
     * @return
     */
    @PluginFactory
    public static AwsS3Config createFluencyConfig(
    		@PluginAttribute(value= "formatType", defaultString= "JSONL") final String formatType,
    		@PluginAttribute(value= "formatCsvColumnNames") final String formatCsvColumnNames,
    		@PluginAttribute(value= "awsEndpoint") final String awsEndpoint,
    		@PluginAttribute(value= "awsRegion") final String awsRegion,
    		@PluginAttribute(value= "awsAccessKeyId") final String awsAccessKeyId,
    		@PluginAttribute(value= "awsSecretAccessKey") final String awsSecretAccessKey,
    		@PluginAttribute(value= "retryMax", defaultInt= 10) final int senderRetryMax,
    		@PluginAttribute(value= "retryIntervalMillis", defaultInt= 1000) final int senderRetryIntervalMillis,
    		@PluginAttribute(value= "maxRetryIntervalMillis", defaultInt= 30000) final int senderMaxRetryIntervalMillis,
    		@PluginAttribute(value= "retryFactor", defaultFloat= 2) final float senderRetryFactor,
    		@PluginAttribute(value= "senderWorkBufSize", defaultInt= 8192) final int senderWorkBufSize,
    		@PluginAttribute(value= "compressionEnabled", defaultBoolean= true) final boolean compressionEnabled,
    		@PluginAttribute(value= "s3KeyPrefix") final String s3KeyPrefix,
    		@PluginAttribute(value= "s3KeySuffix") final String s3KeySuffix,
    		@PluginAttribute(value= "zoneOffsetId", defaultString= "Z") final String zoneOffsetId,
    		@PluginAttribute(value= "maxBufferSize", defaultLong= 536870912) final long maxBufferSize,
    		@PluginAttribute(value= "bufferChunkInitialSize", defaultInt= 1048576) final int bufferChunkInitialSize,
    		@PluginAttribute(value= "bufferChunkRetentionSize", defaultInt= 4194304) final int bufferChunkRetentionSize,
    		@PluginAttribute(value= "bufferChunkRetentionTimeMillis", defaultInt= 1000) final int bufferChunkRetentionTimeMillis,
    		@PluginAttribute(value= "flushAttemptIntervalMillis", defaultInt= 600) final int flushAttemptIntervalMillis,
    		@PluginAttribute("fileBackupDir") final String fileBackupDir,
    		@PluginAttribute(value= "waitUntilBufferFlushed", defaultInt= 10) final int waitUntilBufferFlushed,
    		@PluginAttribute(value= "waitUntilFlusherTerminated", defaultInt= 10) final int waitUntilFlusherTerminated,
    		@PluginAttribute(value= "jvmHeapBufferMode", defaultBoolean= true) final boolean jvmHeapBufferMode,
    		@PluginAttribute("customS3DestinationDecider") final String customS3DestinationDecider) {
    	
    	AwsS3Config config = new AwsS3Config();
    	config.fluencyBuilder = new FluencyBuilderForAwsS3();
    	
    	FluencyBuilderForAwsS3 builder = config.fluencyBuilder;
    	builder.setMaxBufferSize(maxBufferSize);
    	builder.setBufferChunkInitialSize(bufferChunkInitialSize);
    	builder.setBufferChunkRetentionSize(bufferChunkRetentionSize);
    	builder.setBufferChunkRetentionTimeMillis(bufferChunkRetentionTimeMillis);
    	builder.setFlushAttemptIntervalMillis(flushAttemptIntervalMillis);
    	builder.setWaitUntilBufferFlushed(waitUntilBufferFlushed);
    	builder.setWaitUntilFlusherTerminated(waitUntilFlusherTerminated);
    	builder.setFormatType(FormatType.valueOf(formatType));
    	builder.setFormatCsvColumnNames(Arrays.asList(formatCsvColumnNames.split("\\s*,\\s*")));
    	builder.setAwsEndpoint(awsEndpoint);
    	builder.setAwsRegion(awsRegion);
    	builder.setAwsAccessKeyId(awsAccessKeyId);
    	builder.setAwsSecretAccessKey(awsSecretAccessKey);
    	builder.setSenderRetryMax(senderRetryMax);
    	builder.setSenderRetryIntervalMillis(senderRetryIntervalMillis);
    	builder.setSenderMaxRetryIntervalMillis(senderMaxRetryIntervalMillis);
    	builder.setSenderRetryFactor(senderRetryFactor);
    	builder.setSenderWorkBufSize(senderWorkBufSize);
    	builder.setCompressionEnabled(compressionEnabled);
    	builder.setS3KeyPrefix(s3KeyPrefix);
    	builder.setS3KeySuffix(s3KeySuffix);
    	builder.setS3KeyTimeZoneId(ZoneOffset.of(zoneOffsetId));
    	if (Assert.isNonEmpty(customS3DestinationDecider)) {
			try {
				Constructor<?>[] constructors = Class.forName(customS3DestinationDecider).getDeclaredConstructors();
				if (Assert.isNonEmpty(constructors) && Assert.isNonEmpty(constructors[0])) {

					builder.setCustomS3DestinationDecider((S3DestinationDecider) constructors[0].newInstance());

				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException | ClassNotFoundException e) {
				LOGGER.error("Error while setting CustomS3DestinationDecider: {}", e.getMessage());
			}
		}
        return config;
    }
    
    public Fluency makeFluency() {
    	FluencyBuilderForAwsS3 builder = this.fluencyBuilder;
    	if (Assert.isEmpty(builder)) {
			builder = new FluencyBuilderForAwsS3();
			builder.setFormatType(FormatType.JSONL);
		}
    	return builder.build();
    }

}

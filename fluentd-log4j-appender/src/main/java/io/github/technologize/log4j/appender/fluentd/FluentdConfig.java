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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.fluentd.FluencyBuilderForFluentd;

import io.github.technologize.log4j.appender.fluency.core.FluencyConfig;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = "Config", category = Core.CATEGORY_NAME, elementType = FluencyConfig.ELEMENT_TYPE, printObject = true)
public class FluentdConfig implements FluencyConfig {
	
    private Server[] servers;
    private FluencyBuilderForFluentd fluencyBuilder;
    
    @PluginFactory
    public static FluentdConfig createFluencyConfig(
    		@PluginElement(Server.ELEMENT_TYPE) final Server[] servers,
            @PluginAttribute("maxBufferSize") final Long maxBufferSize,
            @PluginAttribute("bufferChunkInitialSize") final Integer bufferChunkInitialSize,
            @PluginAttribute("bufferChunkRetentionSize") final Integer bufferChunkRetentionSize,
            @PluginAttribute("bufferChunkRetentionTimeMillis") final Integer bufferChunkRetentionTimeMillis,
            @PluginAttribute("flushAttemptIntervalMillis") final Integer flushAttemptIntervalMillis,
            @PluginAttribute("waitUntilBufferFlushed") final Integer waitUntilBufferFlushed,
            @PluginAttribute("waitUntilFlusherTerminated") final Integer waitUntilFlusherTerminated,
            @PluginAttribute("senderMaxRetryCount") final Integer senderMaxRetryCount,
            @PluginAttribute("senderBaseRetryIntervalMillis") final Integer senderBaseRetryIntervalMillis,
            @PluginAttribute("senderMaxRetryIntervalMillis") final Integer senderMaxRetryIntervalMillis,
            @PluginAttribute("connectionTimeoutMilli") final Integer connectionTimeoutMilli,
            @PluginAttribute("readTimeoutMilli") final Integer readTimeoutMilli,
            @PluginAttribute("ackResponseMode") final boolean ackResponseMode,
            @PluginAttribute("sslEnabled") final boolean sslEnabled,
            @PluginAttribute("jvmHeapBufferMode") final Boolean jvmHeapBufferMode,
            @PluginAttribute("fileBackupDir") final String fileBackupDir) {
    	
    	FluentdConfig config = new FluentdConfig();
    	config.servers = servers;
    	config.fluencyBuilder = new FluencyBuilderForFluentd();
    	
    	FluencyBuilderForFluentd builder = config.fluencyBuilder;
    	builder.setMaxBufferSize(maxBufferSize);
    	builder.setBufferChunkInitialSize(bufferChunkInitialSize);
    	builder.setBufferChunkRetentionSize(bufferChunkRetentionSize);
    	builder.setBufferChunkRetentionTimeMillis(bufferChunkRetentionTimeMillis);
    	builder.setFlushAttemptIntervalMillis(flushAttemptIntervalMillis);
    	builder.setWaitUntilBufferFlushed(waitUntilBufferFlushed);
    	builder.setWaitUntilFlusherTerminated(waitUntilFlusherTerminated);
    	builder.setSenderMaxRetryCount(senderMaxRetryCount);
    	builder.setSenderBaseRetryIntervalMillis(senderBaseRetryIntervalMillis);
    	builder.setSenderMaxRetryIntervalMillis(senderMaxRetryIntervalMillis);
    	builder.setConnectionTimeoutMilli(connectionTimeoutMilli);
    	builder.setReadTimeoutMilli(readTimeoutMilli);
    	builder.setAckResponseMode(ackResponseMode);
    	builder.setSslEnabled(sslEnabled);
    	builder.setJvmHeapBufferMode(jvmHeapBufferMode);
    	builder.setFileBackupDir(fileBackupDir);
        return config;
    }
    
    public Fluency makeFluency() {
    	List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
    	for (Server server : servers) {
			addresses.add(server.getAddress());
		}
    	return fluencyBuilder.build(addresses);
    }

}

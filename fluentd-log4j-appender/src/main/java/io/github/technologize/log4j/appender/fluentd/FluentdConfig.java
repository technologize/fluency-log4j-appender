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
import java.util.Objects;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Assert;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.fluentd.FluencyBuilderForFluentd;

import io.github.technologize.log4j.appender.fluency.core.FluencyConfig;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = FluentdConfig.PLUGIN_TYPE, category = Core.CATEGORY_NAME, elementType = FluencyConfig.ELEMENT_TYPE, printObject = true)
public class FluentdConfig implements FluencyConfig {
	
	public static final String PLUGIN_TYPE = "FluentdConfig";
	
    private Server[] servers;
    private FluencyBuilderForFluentd fluencyBuilder;
    
    @PluginFactory
    public static FluentdConfig createFluencyConfig(
    		@PluginElement(Server.ELEMENT_TYPE) final Server[] servers,
            @PluginAttribute("maxBufferSize") final long maxBufferSize,
            @PluginAttribute("bufferChunkInitialSize") final int bufferChunkInitialSize,
            @PluginAttribute("bufferChunkRetentionSize") final int bufferChunkRetentionSize,
            @PluginAttribute("bufferChunkRetentionTimeMillis") final int bufferChunkRetentionTimeMillis,
            @PluginAttribute("flushAttemptIntervalMillis") final int flushAttemptIntervalMillis,
            @PluginAttribute("waitUntilBufferFlushed") final int waitUntilBufferFlushed,
            @PluginAttribute("waitUntilFlusherTerminated") final int waitUntilFlusherTerminated,
            @PluginAttribute("senderMaxRetryCount") final int senderMaxRetryCount,
            @PluginAttribute("senderBaseRetryIntervalMillis") final int senderBaseRetryIntervalMillis,
            @PluginAttribute("senderMaxRetryIntervalMillis") final int senderMaxRetryIntervalMillis,
            @PluginAttribute("connectionTimeoutMillis") final int connectionTimeoutMillis,
            @PluginAttribute("readTimeoutMillis") final int readTimeoutMillis,
            @PluginAttribute("ackResponseMode") final boolean ackResponseMode,
            @PluginAttribute("sslEnabled") final boolean sslEnabled,
            @PluginAttribute("jvmHeapBufferMode") final boolean jvmHeapBufferMode,
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
    	builder.setConnectionTimeoutMilli(connectionTimeoutMillis);
    	builder.setReadTimeoutMilli(readTimeoutMillis);
    	builder.setAckResponseMode(ackResponseMode);
    	builder.setSslEnabled(sslEnabled);
    	builder.setJvmHeapBufferMode(jvmHeapBufferMode);
    	builder.setFileBackupDir(fileBackupDir);
        return config;
    }
    
    public Fluency makeFluency() {
    	FluencyBuilderForFluentd builder = Objects.requireNonNullElse(this.fluencyBuilder, new FluencyBuilderForFluentd());
    	
    	if (Assert.isNonEmpty(servers)) {
        	List<InetSocketAddress> addresses = new ArrayList<>();
			for (Server server : this.servers) {
				addresses.add(server.getAddress());
			}
			return builder.build(addresses);
		}
    	
    	return builder.build();
    }

}

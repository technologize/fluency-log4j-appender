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
            @PluginAttribute(value= "maxBufferSize", defaultLong= 536870912) final long maxBufferSize,
            @PluginAttribute(value= "bufferChunkInitialSize", defaultInt= 1048576) final int bufferChunkInitialSize,
            @PluginAttribute(value= "bufferChunkRetentionSize", defaultInt= 4194304) final int bufferChunkRetentionSize,
            @PluginAttribute(value= "bufferChunkRetentionTimeMillis", defaultInt= 1000) final int bufferChunkRetentionTimeMillis,
            @PluginAttribute(value= "flushAttemptIntervalMillis", defaultInt= 600) final int flushAttemptIntervalMillis,
            @PluginAttribute(value= "waitUntilBufferFlushed", defaultInt= 10) final int waitUntilBufferFlushed,
            @PluginAttribute(value= "waitUntilFlusherTerminated", defaultInt= 10) final int waitUntilFlusherTerminated,
            @PluginAttribute(value= "senderMaxRetryCount", defaultInt= 8) final int senderMaxRetryCount,
            @PluginAttribute(value= "senderBaseRetryIntervalMillis", defaultInt= 400) final int senderBaseRetryIntervalMillis,
            @PluginAttribute(value= "senderMaxRetryIntervalMillis", defaultInt= 30000) final int senderMaxRetryIntervalMillis,
            @PluginAttribute(value= "connectionTimeoutMillis", defaultInt= 5000) final int connectionTimeoutMillis,
            @PluginAttribute(value= "readTimeoutMillis", defaultInt= 5000) final int readTimeoutMillis,
            @PluginAttribute(value= "ackResponseMode") final boolean ackResponseMode,
            @PluginAttribute(value= "sslEnabled") final boolean sslEnabled,
            @PluginAttribute(value= "jvmHeapBufferMode", defaultBoolean= true) final boolean jvmHeapBufferMode,
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

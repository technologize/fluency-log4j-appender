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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * @author Bharat Gadde
 *
 */
@Plugin(name = Server.PLUGIN_NAME, category = Node.CATEGORY, elementType = Server.ELEMENT_TYPE, printObject = true)
public class Server {

	public static final String PLUGIN_NAME = "Server";

	public static final String ELEMENT_TYPE = "sever";
	
    private static final Logger LOGGER = StatusLogger.getLogger();

    private final InetSocketAddress address;

    private Server(final String host, final int port) {
        this.address = new InetSocketAddress(host, port);
        
    }

    @PluginFactory
    public static Server createServer(@PluginAttribute("host") @Required(message = "Property host cannot be null") final String host,
            @PluginAttribute("port") @Required(message = "Property port must be > 0") final int port) {
        if (host == null) {
            LOGGER.error("Property host cannot be null");
        }
        if (port <= 0) {
            LOGGER.error("Property port must be > 0");
        }
        return new Server(host, port);
    }

	public InetSocketAddress getAddress() {
		return address;
	}
}

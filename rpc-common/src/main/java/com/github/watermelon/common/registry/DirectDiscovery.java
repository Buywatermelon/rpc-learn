package com.github.watermelon.common.registry;

import com.github.watermelon.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 本地直连 服务发现
 *
 * @author watermelon
 * @since 1.0.0
 */
public class DirectDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectDiscovery.class);

    private final String serverAddress;

    public DirectDiscovery(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public List<String> discover(String serviceName) {
        LOGGER.debug("discovery service {} address: {}", serviceName, serverAddress);

        return Collections.singletonList(serverAddress);
    }
}

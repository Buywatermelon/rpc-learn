package com.github.watermelon.common.registry;

import com.github.watermelon.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectDiscovery.class);

    private final String serverAddress;

    public DirectDiscovery(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public String discover(String serviceName) {
        LOGGER.debug("discovery service {} address: {}", serviceName, serverAddress);

        return serverAddress;
    }
}

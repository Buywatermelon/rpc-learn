package com.github.watermelon.common.registry;

import com.github.watermelon.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地直连服务注册
 *
 * @author watermelon
 * @since 1.0.0
 */
public class DirectRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectRegistry.class);

    @Override
    public void register(String serviceName, String serviceAddress) {
        LOGGER.debug("registry direct");
    }
}

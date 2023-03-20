package com.github.watermelon.registry;

/**
 * 服务发现接口
 *
 * @author watermelon
 * @since 1.0.0
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     */
    String discover(String serviceName);
}

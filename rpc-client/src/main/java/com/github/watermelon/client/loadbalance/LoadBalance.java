package com.github.watermelon.client.loadbalance;

import com.github.watermelon.common.bean.RpcRequest;

import java.util.Collection;

/**
 * 负载均衡
 *
 * @author watermelon
 * @since 1.0.0
 */
public interface LoadBalance {

    String select(Collection<String> anyServerAddress, RpcRequest request);
}

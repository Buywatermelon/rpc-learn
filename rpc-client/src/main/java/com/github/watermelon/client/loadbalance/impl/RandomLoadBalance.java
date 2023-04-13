package com.github.watermelon.client.loadbalance.impl;

import com.github.watermelon.client.loadbalance.LoadBalance;
import com.github.watermelon.common.bean.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机 负载均衡
 *
 * @author watermelon
 * @since 1.0.0
 */
public class RandomLoadBalance implements LoadBalance {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomLoadBalance.class);

    @Override
    public String select(Collection<String> anyServerAddress, RpcRequest request) {
        if (CollectionUtils.isEmpty(anyServerAddress)) {
            throw new RuntimeException("can not find any service");
        }

        // 获取 address 节点
        String address;
        int size = anyServerAddress.size();
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            address = String.valueOf(anyServerAddress.toArray()[0]);
            LOGGER.debug("get only address node: {}", address);
        } else {
            address = String.valueOf(anyServerAddress.toArray()[ThreadLocalRandom.current().nextInt(size)]);
            LOGGER.debug("get random address node: {}", address);
        }

        return address;
    }
}

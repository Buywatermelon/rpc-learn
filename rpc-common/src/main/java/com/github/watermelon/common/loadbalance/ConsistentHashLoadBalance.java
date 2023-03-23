package com.github.watermelon.common.loadbalance;

import com.github.watermelon.common.bean.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.CRC32;

/**
 * 一致性hash 负载均衡
 *
 * @author watermelon
 * @since 1.0.0
 */
public class ConsistentHashLoadBalance implements LoadBalance {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsistentHashLoadBalance.class);

    // 哈希函数
    private final CRC32 crc32 = new CRC32();

    // 计算字符串的CRC32哈希值，并转换为无符号整数
    private long hash(String str) {
        crc32.reset();
        crc32.update(str.getBytes());
        return crc32.getValue() & 0xffffffffL;
    }

    @Override
    public String select(Collection<String> anyServerAddress, RpcRequest request) {
        TreeMap<Long, String> virtualNodeMap = new TreeMap<>();
        for (String node : anyServerAddress) {
            for (int i = 0; i < 256; i++) {
                String virtualNodeName = node + "#" + i; // 虚拟节点名称
                long hashValue = hash(virtualNodeName); // 计算虚拟节点的hash值
                virtualNodeMap.put(hashValue, node); // 将虚拟节点和对应的物理节点放入映射表中
            }
        }

        StringBuilder key = new StringBuilder(request.getInterfaceName() + ":" + request.getMethodName() + ":");
        for (Object parameter : request.getParameters()) {
            key.append(":").append(parameter);
        }

        long hashValue = hash(key.toString());
        // 获取大于等于该hash的子映射表
        SortedMap<Long, String> subMap = virtualNodeMap.tailMap(hashValue);
        if (CollectionUtils.isEmpty(subMap)) { // 子映射表为空，返回第一个虚拟节点对应的物理节点
            return virtualNodeMap.get(virtualNodeMap.firstKey());
        } else {
            return subMap.get(subMap.firstKey()); // 子隐射表不为空，返回第一个虚拟节点对应的物理节点（顺时针方向）
        }
    }
}

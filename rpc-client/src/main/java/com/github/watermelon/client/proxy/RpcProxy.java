package com.github.watermelon.client.proxy;

import com.github.watermelon.client.loadbalance.LoadBalance;
import com.github.watermelon.registry.ServiceDiscovery;
import com.github.watermelon.common.bean.RpcRequest;
import com.github.watermelon.common.bean.RpcResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

/**
 * RPC 代理（用于创建RPC服务代理）
 *
 * @author watermelon
 * @since 1.0.0
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    private LoadBalance loadBalance;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery, LoadBalance loadBalance) {
        this.serviceDiscovery = serviceDiscovery;
        this.loadBalance = loadBalance;
    }

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    // 创建 RPC 请求对象并设置请求属性
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setInterfaceName(method.getDeclaringClass().getName());
                    request.setServiceVersion(serviceVersion);
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);
                    // 获取 RPC 服务地址
                    if (serviceDiscovery != null) {
                        String serviceName = interfaceClass.getName();
                        if (StringUtils.isNotEmpty(serviceVersion)) {
                            serviceName += "-" + serviceVersion;
                        }
                        serviceAddress = loadBalance.select(serviceDiscovery.discover(serviceName), request);
                        LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                    }
                    if (StringUtils.isEmpty(serviceAddress)) {
                        throw new RuntimeException("server address is empty");
                    }
                    // 从 RPC 服务地址中解析主机名与端口号
                    String[] array = StringUtils.split(serviceAddress, ":");
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);
                    // 创建 RPC 客户端对象并发送 RPC 请求
                    RpcClient client = new RpcClient(host, port);
                    long time = System.currentTimeMillis();
                    RpcResponse response = client.send(request);
                    LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
                    if (response == null) {
                        throw new RuntimeException("response is null");
                    }
                    // 返回 RPC 响应结果
                    if (response.hasException()) {
                        return response.getException();
                    } else if (!Objects.equals(response.getRequestId(), request.getRequestId())) {
                        response.setException(new RuntimeException("requestId error"));
                        return response.getException();
                    } else {
                        return response.getResult();
                    }
                }
        );
    }
}

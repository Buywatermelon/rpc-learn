package com.github.watermelon.common.idle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端心跳检测处理器
 *
 * @author watermelon
 * @since 1.0.0
 */
public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

    private static final int MAX_HEARTBEAT_COUNT = 3;

    private final ConcurrentHashMap<Channel, AtomicInteger> channelHeartbeatCountMap = new ConcurrentHashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            sendHeartbeat(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client channel inactive");
        channelHeartbeatCountMap.remove(ctx.channel());
        ctx.fireChannelInactive();
    }

    private void sendHeartbeat(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                .addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("client send heartbeat to server");
                        channelHeartbeatCountMap.getOrDefault(ctx.channel(), new AtomicInteger(0)).set(0);
                    } else {
                        channelHeartbeatCountMap.putIfAbsent(ctx.channel(), new AtomicInteger(1));
                        int failureCount = channelHeartbeatCountMap.get(ctx.channel()).incrementAndGet();
                        if (failureCount > MAX_HEARTBEAT_COUNT) {
                            logger.error("client send heartbeat to server failed more than 3 times, close channel");
                            channelHeartbeatCountMap.remove(ctx.channel());
                            ctx.close();
                        }
                    }
                });
    }
}

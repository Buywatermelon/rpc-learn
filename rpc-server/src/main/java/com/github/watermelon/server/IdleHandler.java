package com.github.watermelon.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    System.out.println("READER_IDLE");
                    break;
                case WRITER_IDLE:
                    System.out.println("WRITER_IDLE");
                    break;
                case ALL_IDLE:
                    System.out.println("ALL_IDLE");
                    break;
                default:
                    break;
            }
        }
    }
}

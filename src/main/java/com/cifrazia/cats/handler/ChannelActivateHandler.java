package com.cifrazia.cats.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import static com.cifrazia.cats.CatsConnect.CATS_VERSION;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelActivateHandler extends ChannelInboundHandlerAdapter {
    private final ReadProtocolVersionHandler readProtocolVersionHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.nextHandler(ctx.pipeline());
        ctx.writeAndFlush(Unpooled.copyInt(CATS_VERSION.intValue()));
    }

    private void nextHandler(ChannelPipeline pipeline) {
        pipeline.remove(this);
        pipeline.addFirst(this.readProtocolVersionHandler);
    }
}

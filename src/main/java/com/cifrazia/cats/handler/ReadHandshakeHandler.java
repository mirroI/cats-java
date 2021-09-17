package com.cifrazia.cats.handler;

import com.cifrazia.cats.api.MessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadHandshakeHandler extends ChannelInboundHandlerAdapter {
    private final ChannelReadHandler channelReadHandler;
    private final Runnable channelAuthenticate;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean status = ((ByteBuf) msg).readBoolean();

        if (status) {
            this.nextHandler(ctx.pipeline());
            this.channelAuthenticate.run();
        }
    }

    private void nextHandler(ChannelPipeline pipeline) {
        pipeline.remove(this);
        pipeline.addFirst(new MessageDecoder(), this.channelReadHandler);
    }
}

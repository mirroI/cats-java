package com.cifrazia.cats.handler;

import com.cifrazia.cats.model.statement.StatementRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.Clock;

import static com.cifrazia.cats.api.jackson.SerDes.OBJECT_MAPPER;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadProtocolVersionHandler extends ChannelInboundHandlerAdapter {
    private final UnsignedInteger apiVersion;
    private final ReadStatementHandler readStatementHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = ((ByteBuf) msg);

        if (byteBuf.readableBytes() >= 4) {
            UnsignedInteger serverCatsVersion = UnsignedInteger.valueOf(byteBuf.readInt());

            if (serverCatsVersion.longValue() == 0) {
                this.nextHandler(ctx.pipeline());
                this.writeStatement(ctx.channel());
            }
        }
    }

    private void nextHandler(ChannelPipeline pipeline) {
        pipeline.remove(this);
        pipeline.addFirst(this.readStatementHandler);
    }

    private void writeStatement(Channel channel) throws JsonProcessingException {
        StatementRequest statement = StatementRequest.builder()
                .apiVersion(this.apiVersion)
                .clientTime(Clock.systemUTC().millis())
                .build();
        byte[] statementBytes = OBJECT_MAPPER.writeValueAsBytes(statement);

        channel.write(Unpooled.copyInt(statementBytes.length));
        channel.write(Unpooled.copiedBuffer(statementBytes));
        channel.flush();
    }
}

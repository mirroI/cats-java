package com.cifrazia.cats.handler;

import com.cifrazia.cats.model.statement.StatementResponse;
import com.google.common.hash.Hashing;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.*;

import java.nio.charset.StandardCharsets;
import java.time.*;

import static com.cifrazia.cats.api.jackson.SerDes.OBJECT_MAPPER;

public class ReadStatementHandler extends ChannelInboundHandlerAdapter {
    private final String secretKey;
    private final ReadHandshakeHandler readHandshakeHandler;

    private int statementLength = 0;
    private long timeDeference = 0;

    @Builder
    private ReadStatementHandler(String secretKey, ReadHandshakeHandler readHandshakeHandler) {
        this.secretKey = secretKey;
        this.readHandshakeHandler = readHandshakeHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = ((ByteBuf) msg);

        if (statementLength == 0 && byteBuf.readableBytes() >= 4) {
            statementLength = byteBuf.readInt();
        }

        if (statementLength != 0 && byteBuf.readableBytes() >= statementLength) {
            byte[] statementBytes = new byte[statementLength];
            byteBuf.readBytes(statementBytes);

            StatementResponse statement = OBJECT_MAPPER.readValue(statementBytes, StatementResponse.class);
            timeDeference = Clock.systemUTC().millis() - statement.getServerTime();

            this.nextHandler(ctx.pipeline());
            this.writeHandshake(ctx.channel());

            statementLength = 0;
        }
    }

    private void nextHandler(ChannelPipeline pipeline) {
        pipeline.remove(this);
        pipeline.addFirst(this.readHandshakeHandler);
    }

    private void writeHandshake(Channel channel) {
        long time = getServerTime().toEpochSecond(ZoneOffset.UTC) / 10 * 10;
        byte[] handshake = Hashing.sha256().hashString((secretKey + time), StandardCharsets.UTF_8).asBytes();

        channel.writeAndFlush(Unpooled.copiedBuffer(handshake));
    }

    public LocalDateTime getServerTime() {
        return Instant.now().plusMillis(this.timeDeference).atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}

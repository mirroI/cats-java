package com.cifrazia.cats.model.request;

import com.cifrazia.cats.model.header.AbstractHeader;
import com.cifrazia.cats.model.header.InputHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Builder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.cifrazia.cats.api.jackson.SerDes.OBJECT_MAPPER;

public class BasicRequest extends Request {
    private ByteBuf data;

    @Builder
    public BasicRequest(AbstractHeader header, Map<String, Object> messageHeader, ByteBuf data, Map<String, Object> metaData, long timeOut) {
        super(header, messageHeader, metaData, timeOut);

        this.data = data;
    }

    @Override
    public ByteBuf toByteBuf() throws JsonProcessingException {
        ByteBuf messageHeaderByteBuf = Unpooled.copiedBuffer(OBJECT_MAPPER.writeValueAsString(getMessageHeader()), StandardCharsets.UTF_8);
        ((InputHeader) getHeader()).setDataLength(
                UnsignedInteger.valueOf(messageHeaderByteBuf.readableBytes() + MESSAGE_HEADER_SPLITTER.length + data.readableBytes()));

        ByteBuf headerByteBuf = super.getHeader().toByteBuf();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(headerByteBuf, messageHeaderByteBuf, Unpooled.wrappedBuffer(MESSAGE_HEADER_SPLITTER), this.data);

        return byteBuf;
    }
}

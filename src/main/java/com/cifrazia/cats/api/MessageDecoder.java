package com.cifrazia.cats.api;

import com.cifrazia.cats.api.buffers.CatsByteBuf;
import com.cifrazia.cats.api.buffers.MessageByteBuf;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.enumiration.HeaderType;
import com.cifrazia.cats.model.response.AbstractResponse;
import com.cifrazia.cats.model.response.BasicResponse;
import com.cifrazia.cats.model.header.AbstractHeader;
import com.cifrazia.cats.model.header.BasicHeader;
import com.cifrazia.cats.model.header.Header;
import com.cifrazia.cats.model.header.InputHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cifrazia.cats.api.jackson.SerDes.OBJECT_MAPPER;
import static com.cifrazia.cats.model.AbstractMessage.MESSAGE_HEADER_SPLITTER;

public class MessageDecoder extends ByteToMessageDecoder {
    private HeaderType headerType = HeaderType.EMPTY;
    private AbstractHeader header = null;
    private int messageHeaderSize = 0;
    private Map<String, Object> messageHeader = null;
    private CatsByteBuf byteBuf = null;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() >= 1) {
            if (this.headerType == HeaderType.EMPTY) {
                this.headerType = HeaderType.valueOf(byteBuf.readByte());
            }

            if (this.header == null) {
                if (this.readHeader(byteBuf)) {
                    if (this.headerType == HeaderType.SPEED_LIMIT || this.headerType == HeaderType.CANCEL_INPUT || this.headerType == HeaderType.PING_PONG) {
                        this.messageCollected();
                        return;
                    }
                } else {
                    return;
                }
            }

            if (this.messageHeader == null) {
                if (!this.readMessageHeader(byteBuf)) {
                    return;
                }

                if (this.headerType == HeaderType.BASIC || this.headerType == HeaderType.CHILDREN) {
                    InputHeader header = (InputHeader) this.header;

                    if (header.getDataLength().longValue() - this.messageHeaderSize - MESSAGE_HEADER_SPLITTER.length == 0) {
                        list.add(this.messageCollected());
                        return;
                    }
                }
            }

            if (this.byteBuf == null) {
                if (!this.initByteBuf()) {
                    return;
                }
            }

            if (this.byteBuf.isWrite()) {
                if (this.byteBuf.getEmptyLength().longValue() > 0) {
                    this.byteBuf.write(byteBuf);

                    if (!this.byteBuf.isWrite()) {
                        list.add(this.messageCollected());
                    }
                }
            }
        }
//
//        if (byteBuf.readableBytes() > 0) {
//            decode(channelHandlerContext, byteBuf, list);
//        }
    }

    private boolean readHeader(ByteBuf byteBuf) {
        switch (headerType) {
            case BASIC: {
                this.header = new BasicHeader(headerType, byteBuf);
                break;
            }
        }

        return header != null;
    }

    private boolean readMessageHeader(ByteBuf byteBuf) {
        if (this.headerType == HeaderType.BASIC || this.headerType == HeaderType.CHILDREN) {
            InputHeader header = (InputHeader) this.header;

            byte[] bytes = new byte[Math.min(byteBuf.readableBytes(), header.getDataLength().intValue())];
            byteBuf.asReadOnly().readBytes(bytes);

            this.messageHeaderSize = Bytes.indexOf(bytes, MESSAGE_HEADER_SPLITTER);

            if (this.messageHeaderSize >= 0) {
                try {
                    this.messageHeader = OBJECT_MAPPER.readValue(byteBuf.readBytes(this.messageHeaderSize)
                            .toString(StandardCharsets.UTF_8), Map.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                byteBuf.readBytes(MESSAGE_HEADER_SPLITTER.length).clear();

                return true;
            }
        } else {

        }

        return false;
    }

    private boolean initByteBuf() {
        if (this.headerType == HeaderType.BASIC || this.headerType == HeaderType.CHILDREN) {
            InputHeader header = (InputHeader) this.header;

            this.byteBuf = new MessageByteBuf(header.getCompressionType());
            this.byteBuf.initial(UnsignedInteger.valueOf(header.getDataLength().longValue() - messageHeaderSize - MESSAGE_HEADER_SPLITTER.length));

            return true;
        }

        return false;
    }

    private AbstractResponse messageCollected() {
        AbstractResponse response = null;

        if (this.headerType == HeaderType.BASIC || this.headerType == HeaderType.CHILDREN || this.headerType == HeaderType.STREAM) {
            Header header = (Header) this.header;

            if (header.getDataType() == DataType.FILES) {

            } else {
                ByteBuf byteBuf;

                if (this.byteBuf != null) {
                    byteBuf = this.byteBuf.read();
                } else {
                    byteBuf = Unpooled.buffer();
                }

                response = new BasicResponse(header, new LinkedHashMap<>(this.messageHeader), byteBuf);
            }
        }

        this.headerType = HeaderType.EMPTY;
        this.header = null;
        this.messageHeader = null;
        this.byteBuf = null;

        return response;
    }
}

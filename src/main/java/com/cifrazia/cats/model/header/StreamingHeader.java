package com.cifrazia.cats.model.header;

import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.enumiration.HeaderType;
import com.google.common.primitives.UnsignedLong;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StreamingHeader extends Header {
    @Setter(AccessLevel.NONE)
    private UnsignedShort handlerId;
    private long time;

    public StreamingHeader(UnsignedShort handlerId, DataType dataType, CompressionType compressionType) {
        super(dataType, compressionType, HeaderType.STREAM);

        this.handlerId = handlerId;
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer(15);

        byteBuf.writeByte(super.getHeaderType().getValue());
        byteBuf.writeShort(this.handlerId.intValue());
        byteBuf.writeShort(super.getMessageId().intValue());
        byteBuf.writeLong(this.time);
        byteBuf.writeByte(super.getDataType().getValue());
        byteBuf.writeByte(super.getCompressionType().getValue());

        return byteBuf;
    }
}

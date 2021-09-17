package com.cifrazia.cats.model.header;

import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.enumiration.HeaderType;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;

@Getter
public class InputHeader extends Header {
    @Setter
    private UnsignedInteger dataLength;

    protected InputHeader(HeaderType headerType) {
        super(headerType);
    }

    protected InputHeader(DataType dataType, CompressionType compressionType, HeaderType headerType) {
        super(dataType, compressionType, headerType);
    }

    public InputHeader(DataType dataType, CompressionType compressionType) {
        this(dataType, compressionType, HeaderType.CHILDREN);
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer(9);

        byteBuf.writeByte(super.getHeaderType().getValue());
        byteBuf.writeShort(super.getMessageId().intValue());
        byteBuf.writeByte(super.getDataType().getValue());
        byteBuf.writeByte(super.getCompressionType().getValue());
        byteBuf.writeInt(this.dataLength.intValue());

        return byteBuf;
    }
}

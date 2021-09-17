package com.cifrazia.cats.model.header;

import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.enumiration.HeaderType;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BasicHeader extends InputHeader {
    private UnsignedShort handlerId;
    @Setter
    private long time;

    public BasicHeader(UnsignedShort handlerId, DataType dataType, CompressionType compressionType) {
        super(dataType, compressionType, HeaderType.BASIC);

        this.handlerId = handlerId;
    }

    public BasicHeader(HeaderType headerType, ByteBuf byteBuf) {
        super(headerType);

        this.handlerId = UnsignedShort.valueOf(byteBuf.readUnsignedShort());
        super.setMessageId(UnsignedShort.valueOf(byteBuf.readUnsignedShort()));
        this.time = byteBuf.readLong();
        super.setDataType(DataType.valueOf(byteBuf.readByte()));
        super.setCompressionType(CompressionType.valueOf(byteBuf.readByte()));
        super.setDataLength(UnsignedInteger.valueOf(byteBuf.readUnsignedInt()));
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer(19);

        byteBuf.writeByte(super.getHeaderType().getValue());
        byteBuf.writeShort(this.handlerId.intValue());
        byteBuf.writeShort(super.getMessageId().intValue());
        byteBuf.writeLong(this.time);
        byteBuf.writeByte(super.getDataType().getValue());
        byteBuf.writeByte(super.getCompressionType().getValue());
        byteBuf.writeInt(super.getDataLength().intValue());

        return byteBuf;
    }
}

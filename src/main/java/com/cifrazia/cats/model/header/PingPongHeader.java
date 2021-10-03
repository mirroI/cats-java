package com.cifrazia.cats.model.header;

import com.cifrazia.cats.enumiration.HeaderType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PingPongHeader extends AbstractHeader {
    private long time;

    public PingPongHeader() {
        super(HeaderType.PING_PONG);
    }

    public PingPongHeader(HeaderType headerType, ByteBuf byteBuf) {
        super(headerType);

        this.time = byteBuf.readLong();
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer(9);

        byteBuf.writeByte(super.getHeaderType().getValue());
        byteBuf.writeLong(this.time);

        return byteBuf;
    }
}

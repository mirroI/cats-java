package com.cifrazia.cats.api.buffers;

import com.cifrazia.cats.enumiration.CompressionType;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public abstract class CatsByteBuf {
    @Getter(AccessLevel.NONE)
    protected final CompressionType compressionType;
    protected boolean write = true;
    protected UnsignedInteger emptyLength = UnsignedInteger.ZERO;

    protected CatsByteBuf(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    public abstract void initial(UnsignedInteger bufferSize);
    public abstract UnsignedInteger write(ByteBuf byteBuf);
    public abstract ByteBuf read();
}

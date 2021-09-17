package com.cifrazia.cats.model.header;

import com.cifrazia.cats.enumiration.HeaderType;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractHeader {
    private HeaderType headerType;

    public abstract ByteBuf toByteBuf();
}

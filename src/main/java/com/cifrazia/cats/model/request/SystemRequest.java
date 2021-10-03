package com.cifrazia.cats.model.request;

import com.cifrazia.cats.model.header.AbstractHeader;
import io.netty.buffer.ByteBuf;

public class SystemRequest extends AbstractRequest {
    public SystemRequest(AbstractHeader header) {
        super(header);
    }

    @Override
    public ByteBuf toByteBuf() {
        return super.getHeader().toByteBuf();
    }
}

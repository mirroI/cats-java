package com.cifrazia.cats.model.request;

import com.cifrazia.cats.model.AbstractMessage;
import com.cifrazia.cats.model.header.AbstractHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;

public abstract class AbstractRequest extends AbstractMessage {
    public AbstractRequest(AbstractHeader header) {
        super(header);
    }

    public abstract ByteBuf toByteBuf() throws JsonProcessingException;
}

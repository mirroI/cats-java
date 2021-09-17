package com.cifrazia.cats.model.response;

import com.cifrazia.cats.model.header.AbstractHeader;
import com.cifrazia.cats.model.header.InputHeader;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.util.Map;

public class BasicResponse extends Response {
    @Getter
    private ByteBuf data;

    public BasicResponse(AbstractHeader header, Map<String, Object> messageHeader, ByteBuf data) {
        super(header, messageHeader);

        this.data = data;
    }

    @Override
    public InputHeader getHeader() {
        return (InputHeader) super.getHeader();
    }
}

package com.cifrazia.cats.model.response;

import com.cifrazia.cats.model.header.AbstractHeader;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class Response extends AbstractResponse {
    private final Map<String, Object> messageHeader;

    public Response(AbstractHeader header, Map<String, Object> messageHeader) {
        super(header);

        this.messageHeader = messageHeader;
    }
}

package com.cifrazia.cats.model.response;

import com.cifrazia.cats.model.AbstractMessage;
import com.cifrazia.cats.model.header.AbstractHeader;

public class AbstractResponse extends AbstractMessage {
    public AbstractResponse(AbstractHeader header) {
        super(header);
    }
}

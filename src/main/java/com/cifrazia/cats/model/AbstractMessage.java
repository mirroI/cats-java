package com.cifrazia.cats.model;

import com.cifrazia.cats.model.header.AbstractHeader;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AbstractMessage {
    public static final byte[] MESSAGE_HEADER_SPLITTER = new byte[]{0, 0};

    private AbstractHeader header;
}

package com.cifrazia.cats.enumiration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeaderType {
    EMPTY((byte) -1),
    BASIC((byte) 0x0),
    STREAM((byte) 0x1),
    CHILDREN((byte) 0x2),
    SPEED_LIMIT((byte) 0x5),
    CANCEL_INPUT((byte) 0x6),
    PING_PONG ((byte) 0xFF);

    private byte value;

    public static HeaderType valueOf(byte value) {
        for (HeaderType headerType : values()) {
            if (headerType.value == value) {
                return headerType;
            }
        }

        return  null;
    }
}

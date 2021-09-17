package com.cifrazia.cats.enumiration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompressionType {
    NONE((byte) 0x0),
    GZIP((byte) 0x1),
    ZLIB((byte) 0x2);

    private byte value;

    public static CompressionType valueOf(byte value) {
        for (CompressionType compressionType : values()) {
            if (compressionType.value == value) {
                return compressionType;
            }
        }

        return  null;
    }
}

package com.cifrazia.cats.enumiration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataType {
    BYTES((byte) 0x0),
    JSON((byte) 0x1),
    FILES((byte) 0x2);

    private byte value;

    public static DataType valueOf(byte value) {
        for (DataType dataType : values()) {
            if (dataType.value == value) {
                return dataType;
            }
        }

        return  null;
    }
}

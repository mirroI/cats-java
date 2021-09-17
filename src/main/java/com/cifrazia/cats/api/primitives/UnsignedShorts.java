package com.cifrazia.cats.api.primitives;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public class UnsignedShorts {
    @CanIgnoreReturnValue
    public static short parseUnsignedShort(String s) {
        return parseUnsignedShort(s, 10);
    }

    @CanIgnoreReturnValue
    public static short parseUnsignedShort(String string, int radix) {
        Preconditions.checkNotNull(string);
        long result = Long.parseLong(string, radix);

        if ((result & 65535) != result) {
            throw new NumberFormatException((new StringBuilder(69 + string.length())).append("Input ").append(string).append(" in base ").append(radix).append(" is not in the range of an unsigned short").toString());
        } else {
            return (short) result;
        }
    }

    public static String toString(short x) {
        return toString(x, 10);
    }

    public static String toString(short x, int radix) {
        int asInt = (int) x & 65535;
        return Integer.toString(asInt, radix);
    }
}

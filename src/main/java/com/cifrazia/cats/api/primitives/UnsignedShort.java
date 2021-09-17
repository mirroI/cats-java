package com.cifrazia.cats.api.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.math.BigInteger;

@GwtCompatible(
        emulated = true
)
public final class UnsignedShort extends Number {
    public static final UnsignedShort ZERO = fromShortBits((short) 0);
    public static final UnsignedShort ONE = fromShortBits((short) 1);
    public static final UnsignedShort MAX_VALUE = fromShortBits((short) -1);
    private final short value;

    private UnsignedShort(short value) {
        this.value = (short) (value & (short) -1);
    }

    public static UnsignedShort fromShortBits(short bits) {
        return new UnsignedShort(bits);
    }

    public static UnsignedShort valueOf(int value) {
        Preconditions.checkArgument((value & 65535) == value, "value (%s) is outside the range for an unsigned short value", value);
        return fromShortBits((short) value);
    }

    public static UnsignedShort valueOf(long value) {
        Preconditions.checkArgument((value & 65535) == value, "value (%s) is outside the range for an unsigned short value", value);
        return fromShortBits((short) value);
    }

    public static UnsignedShort valueOf(BigInteger value) {
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(value.signum() >= 0 && value.bitLength() <= 16, "value (%s) is outside the range for an unsigned short value", value);

        return fromShortBits(value.shortValue());
    }

    public static UnsignedShort valueOf(String string) {
        return valueOf(string, 10);
    }

    public static UnsignedShort valueOf(String string, int radix) {
        return fromShortBits(UnsignedShorts.parseUnsignedShort(string, radix));
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.toString(10);
    }

    public String toString(int radix) {
        return UnsignedShorts.toString(this.value, radix);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof UnsignedShort) {
            return ((UnsignedShort) obj).value == value;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }
}

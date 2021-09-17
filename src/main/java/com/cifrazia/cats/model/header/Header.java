package com.cifrazia.cats.model.header;

import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.enumiration.HeaderType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Header extends AbstractHeader {
    @Setter
    private UnsignedShort messageId;
    @Setter(AccessLevel.PROTECTED)
    private DataType dataType;
    @Setter(AccessLevel.PROTECTED)
    private CompressionType compressionType;

    protected Header(HeaderType headerType) {
        super(headerType);
    }

    protected Header(DataType dataType, CompressionType compressionType, HeaderType headerType) {
        super(headerType);

        this.dataType = dataType;
        this.compressionType = compressionType;
    }
}

package com.cifrazia.cats.api.buffers;

import com.cifrazia.cats.enumiration.CompressionType;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.GzipOptions;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import lombok.SneakyThrows;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MessageByteBuf extends CatsByteBuf {
    private static final UnsignedInteger MAX_BYTE_BUF_SIZE = UnsignedInteger.valueOf(32000);

    private File fileBuf = null;
    private ByteBuf byteBuf = null;

    private UnsignedInteger bufferSize;

    public MessageByteBuf(CompressionType compressionType) {
        super(compressionType);
    }

    @Override
    @SneakyThrows
    public void initial(UnsignedInteger bufferSize) {
        if (bufferSize.longValue() > MAX_BYTE_BUF_SIZE.longValue()) {
            this.fileBuf = File.createTempFile("./temp/", ".cats");
            this.fileBuf.setWritable(true);
        } else {
            this.byteBuf = Unpooled.buffer(0);
        }

        super.emptyLength = bufferSize;
        this.bufferSize = bufferSize;
    }

    @Override
    @SneakyThrows
    public UnsignedInteger write(ByteBuf byteBuf) {
        if (super.write) {
            UnsignedInteger byteArraySize = null;
            ByteBuf tempByteBuf = null;

            if (byteBuf.readableBytes() <= emptyLength.longValue()) {
                tempByteBuf = byteBuf;
                byteArraySize = UnsignedInteger.valueOf(tempByteBuf.readableBytes());
            } else {
                tempByteBuf = byteBuf.readBytes(emptyLength.intValue());
                byteArraySize = UnsignedInteger.valueOf(emptyLength.intValue());
            }

            if (this.fileBuf != null) {
                byte[] bytes = new byte[tempByteBuf.readableBytes()];
                tempByteBuf.readBytes(bytes);

                FileUtils.writeByteArrayToFile(this.fileBuf, bytes, true);
            } else {
                this.byteBuf.writeBytes(tempByteBuf);
            }

            tempByteBuf.clear();
            super.emptyLength = super.emptyLength.minus(byteArraySize);

            if (super.emptyLength.longValue() == 0) {
                super.write = false;

                switch (super.compressionType) {
                    case GZIP: {
                        if (fileBuf != null) {
                            GzipCompressorInputStream in = new GzipCompressorInputStream(new FileInputStream(this.fileBuf));
                            IOUtils.copy(in, new FileOutputStream(this.fileBuf, false));
                        } else {
                            GzipCompressorInputStream in = new GzipCompressorInputStream(new ByteBufInputStream(this.byteBuf));
                            IOUtils.copy(in, new ByteBufOutputStream(this.byteBuf));
                        }

                        break;
                    } case ZLIB: {
                        break;
                    }
                }
            }

            return byteArraySize;
        }

        return null;
    }

    @Override
    @SneakyThrows
    public ByteBuf read() {
        if (fileBuf == null) {
            return byteBuf;
        } else {
            return Unpooled.wrappedBuffer(FileUtils.readFileToByteArray(this.fileBuf));
        }
    }
}

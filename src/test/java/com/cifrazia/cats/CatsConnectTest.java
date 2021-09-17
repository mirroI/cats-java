package com.cifrazia.cats;

import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.model.header.BasicHeader;
import com.cifrazia.cats.model.request.BasicRequest;
import com.cifrazia.cats.model.response.BasicResponse;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.Unpooled;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.Assert.*;

public class CatsConnectTest {
    private CatsConnect catsConnect = null;

    @Before
    public void initConnect() {
//        try {
//            catsConnect = new CatsConnect(UnsignedInteger.ZERO, "secret_key", "185.137.235.92", 9095);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        sendMessage(new BasicRequest(new BasicHeader(UnsignedShort.valueOf(0x0001), DataType.BYTES, CompressionType.NONE), new HashMap<>(), Unpooled.copiedBuffer("hello world".getBytes()), new HashMap<>(), 10))
//                .subscribe(response -> System.out.println((((BasicResponse) response).getData().toString(StandardCharsets.UTF_8))));
//        sendMessage(new BasicRequest(new BasicHeader(UnsignedShort.valueOf(0x0002), DataType.BYTES, CompressionType.NONE), new HashMap<>(), Unpooled.copiedBuffer("hello world".getBytes()), new HashMap<>(), 10)).subscribe(response -> System.out.println((((BasicResponse) response).getData().toString(StandardCharsets.UTF_8))));
//        sendMessage(new BasicRequest(new BasicHeader(UnsignedShort.valueOf(0x0003), DataType.BYTES, CompressionType.NONE), new HashMap<>(), Unpooled.copiedBuffer("hello world".getBytes()), new HashMap<>(), 10)).subscribe(response -> System.out.println((((BasicResponse) response).getData().toString(StandardCharsets.UTF_8))));

    }
}
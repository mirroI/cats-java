package com.cifrazia.cats.handler;

import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.HeaderType;
import com.cifrazia.cats.model.response.AbstractResponse;
import com.cifrazia.cats.model.request.Request;
import com.cifrazia.cats.model.response.Response;
import com.cifrazia.cats.model.header.InputHeader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelReadHandler extends ChannelInboundHandlerAdapter {
    private final Map<UnsignedShort, Request> waitingMap;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AbstractResponse abstractResponse = (AbstractResponse) msg;
        HeaderType headerType = abstractResponse.getHeader().getHeaderType();

        if (headerType == HeaderType.BASIC || headerType == HeaderType.CHILDREN) {
            Response response = (Response) abstractResponse;
            InputHeader header = (InputHeader) response.getHeader();

            Request request = waitingMap.get(header.getMessageId());
            request.getMonoSink().success(response);
        }
    }
}

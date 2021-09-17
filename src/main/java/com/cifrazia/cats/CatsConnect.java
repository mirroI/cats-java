package com.cifrazia.cats;

import com.cifrazia.cats.api.primitives.UnsignedShort;
import com.cifrazia.cats.enumiration.CatsStatus;
import com.cifrazia.cats.enumiration.CompressionType;
import com.cifrazia.cats.enumiration.DataType;
import com.cifrazia.cats.enumiration.HeaderType;
import com.cifrazia.cats.handler.*;
import com.cifrazia.cats.model.header.AbstractHeader;
import com.cifrazia.cats.model.header.BasicHeader;
import com.cifrazia.cats.model.header.InputHeader;
import com.cifrazia.cats.model.header.StreamingHeader;
import com.cifrazia.cats.model.request.AbstractRequest;
import com.cifrazia.cats.model.request.BasicRequest;
import com.cifrazia.cats.model.request.Request;
import com.cifrazia.cats.model.response.BasicResponse;
import com.cifrazia.cats.model.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Builder;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static reactor.core.scheduler.Schedulers.elastic;

public class CatsConnect {
    public static final UnsignedInteger CATS_VERSION = UnsignedInteger.valueOf(2);

    private static final int ID_LIMIT = 32767;
    private static final long PING_AT = 90000l;

    private ChannelFuture channelFuture;
    private CatsStatus status = CatsStatus.DISCONNECTED;

    private final UnsignedInteger apiVersion;
    private final String secretKey;
    private String ip;
    private int port;

    private Runnable connectHandler;
    private Runnable reconnectHandler;

    private long lastWritingAt;

    private Queue<AbstractRequest> writeQueue = new ArrayDeque();
    private Map<UnsignedShort, Request> waitingMap = new HashMap<>();

    private Bootstrap clientBootstrap;
    private EventLoopGroup eventLoopGroup;

    private ChannelReadHandler channelReadHandler;
    private ReadHandshakeHandler readHandshakeHandler;
    private ReadStatementHandler readStatementHandler;
    private ReadProtocolVersionHandler readProtocolVersionHandler;
    private ChannelActivateHandler channelActivateHandler;

    private AtomicInteger incrementMessageId = new AtomicInteger();

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    private ScheduledFuture scheduledFuture = null;
    private AbstractRequest request = null;
    private long requestBytesSent = 0;

    @Builder
    public CatsConnect(UnsignedInteger apiVersion, String secretKey, String ip, int port, Runnable connectHandler, Runnable reconnectHandler)
            throws InterruptedException {
        this.apiVersion = apiVersion;
        this.secretKey = secretKey;
        this.ip = ip;
        this.port = port;

        this.connectHandler = connectHandler;
        this.reconnectHandler = reconnectHandler;

        this.clientBootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup();

        this.clientBootstrap.group(this.eventLoopGroup);
        this.clientBootstrap.channel(NioSocketChannel.class);
        this.clientBootstrap.remoteAddress(new InetSocketAddress(this.ip, this.port));

        this.connect();
    }

    @SneakyThrows
    private void connect() {
        registerHandlers();

        this.clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        reconnect();
                    }
                });
                socketChannel.pipeline().addFirst(CatsConnect.this.channelActivateHandler);
            }
        });

        this.channelFuture = this.clientBootstrap.connect()
                .addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        reconnect();
                    }
                })
                .sync();
    }

    private void reconnect() {
        this.status = CatsStatus.RECONNECTION;

        this.scheduledFuture.cancel(true);
        this.channelFuture = null;

        executorService.schedule(() -> this.connect(), 10, TimeUnit.SECONDS);
    }

    private void registerHandlers() {
        this.channelReadHandler = ChannelReadHandler.builder()
                .waitingMap(waitingMap)
                .build();

        this.readHandshakeHandler = ReadHandshakeHandler.builder()
                .channelReadHandler(this.channelReadHandler)
                .channelAuthenticate(() -> {
                    if (CatsConnect.this.status == CatsStatus.RECONNECTION) {
                        this.reconnectHandler.run();
                    } else {
                        this.connectHandler.run();
                    }

                    CatsConnect.this.status = CatsStatus.CONNECTED;

                    lastWritingAt = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
                    this.scheduledFuture = executorService.scheduleAtFixedRate(() -> writeNextQueue(), 50, 50, TimeUnit.MILLISECONDS);
                })
                .build();

        this.readStatementHandler = ReadStatementHandler.builder()
                .secretKey(this.secretKey)
                .readHandshakeHandler(this.readHandshakeHandler)
                .build();

        this.readProtocolVersionHandler = ReadProtocolVersionHandler.builder()
                .apiVersion(this.apiVersion)
                .readStatementHandler(this.readStatementHandler)
                .build();

        this.channelActivateHandler = ChannelActivateHandler.builder()
                .readProtocolVersionHandler(this.readProtocolVersionHandler)
                .build();
    }

    public Mono<Response> sendMessage(AbstractRequest abstractRequest) {
        Mono mono;

        if (abstractRequest instanceof Request) {
            Request request = (Request) abstractRequest;

            if (request.getHeader().getHeaderType() != HeaderType.CHILDREN) {
                request.getHeader().setMessageId(UnsignedShort.valueOf(this.incrementMessageId.getAndIncrement()));

                if (this.incrementMessageId.get() == ID_LIMIT) {
                    this.incrementMessageId.set(0);
                }
            }

            mono = Mono.create(monoSink -> request.setMonoSink(monoSink)).subscribeOn(elastic());
            waitingMap.put(request.getHeader().getMessageId(), request);
        } else {
            mono = Mono.empty();
        }

        writeQueue.add(abstractRequest);

        return mono;
    }

    protected void ping() {
//        writeQueue.add(new)
    }

    public LocalDateTime getServerTime() {
        return readStatementHandler.getServerTime();
    }

    private void writeNextQueue() {
        Channel channel = channelFuture.channel();

        if (channelFuture.channel().isActive() && this.status == CatsStatus.CONNECTED) {
            if (this.request != null && channel.isWritable()) {
                bytesWritten();
            }

            if (this.request == null) {
                if (writeQueue.isEmpty() && LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() - lastWritingAt > PING_AT) {
                    this.ping();
                    return;
                }

                if (!writeQueue.isEmpty()) {
                    AbstractRequest request = writeQueue.poll();
                    AbstractHeader header = request.getHeader();

                    switch (header.getHeaderType()) {
                        case BASIC: {
                            ((BasicHeader) header).setTime(getServerTime().toInstant(ZoneOffset.UTC).toEpochMilli());
                            break;
                        }
                        case STREAM: {
                            ((StreamingHeader) header).setTime(getServerTime().toInstant(ZoneOffset.UTC).toEpochMilli());
                            break;
                        }
                        case PING_PONG: {
                            ((BasicHeader) header).setTime(getServerTime().toInstant(ZoneOffset.UTC).toEpochMilli());
                            break;
                        }
                    }

                    writeInitial(request);
                    try {
                        channelFuture.channel().writeAndFlush(request.toByteBuf());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writeInitial(AbstractRequest request) {
        this.request = request;

        switch (request.getHeader().getHeaderType()) {
            case BASIC: {
                this.requestBytesSent = -19;
                break;
            }
            case STREAM: {
                this.requestBytesSent = -15;
                break;
            }
            case CHILDREN:
            case PING_PONG: {
                this.requestBytesSent = -9;
                break;
            }
            case SPEED_LIMIT: {
                this.requestBytesSent = -5;
                break;
            }
            case CANCEL_INPUT: {
                this.requestBytesSent = -3;
                break;
            }
        }
    }

    private void bytesWritten() {
        HeaderType headerType = this.request.getHeader().getHeaderType();

        if (headerType == HeaderType.BASIC || headerType == HeaderType.CHILDREN) {
            InputHeader header = (InputHeader) this.request.getHeader();

            if (header.getDataType() == DataType.FILES) {

            }

            //А если это файты, и они не все отправлены?
            this.request = null;
        } else if (headerType == HeaderType.SPEED_LIMIT || headerType == HeaderType.CANCEL_INPUT || headerType == HeaderType.PING_PONG) {
            this.request = null;
        }
    }
}

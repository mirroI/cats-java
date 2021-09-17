package com.cifrazia.cats.model.request;

import com.cifrazia.cats.model.header.AbstractHeader;
import com.cifrazia.cats.model.header.Header;
import com.cifrazia.cats.model.request.AbstractRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.MonoSink;

import java.util.Map;

@Getter
public abstract class Request extends AbstractRequest {
    private final Map<String, Object> messageHeader;
    private final Map<String, Object> metaData;
    private long timeOut;
    @Setter
    private MonoSink<Object> monoSink = null;

    public Request(AbstractHeader header, Map<String, Object> messageHeader, Map<String, Object> metaData, long timeOut) {
        super(header);

        this.messageHeader = messageHeader;
        this.metaData = metaData;
        this.timeOut = timeOut;
    }

    @Override
    public Header getHeader() {
        return (Header) super.getHeader();
    }
}

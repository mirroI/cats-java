package com.cifrazia.cats.model.statement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedInteger;

import com.cifrazia.cats.api.jackson.serializer.UnsignedIntegerSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class StatementRequest {
    @JsonProperty("api")
    @JsonSerialize(using = UnsignedIntegerSerializer.class)
    private final UnsignedInteger apiVersion;
    @JsonProperty("client_time")
    private final long clientTime;
    @JsonProperty("scheme_format")
    private final String schemeFormat = "JSON";
    private final List<String> compressors = Arrays.asList("gzip");
    @JsonProperty("default_compression")
    private final String defaultCompression = "gzip";
}

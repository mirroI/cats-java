package com.cifrazia.cats.api.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.primitives.UnsignedInteger;

import java.io.IOException;

public class UnsignedIntegerDeserializer extends JsonDeserializer<UnsignedInteger> {
    @Override
    public UnsignedInteger deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return UnsignedInteger.valueOf(jsonParser.getLongValue());
    }
}

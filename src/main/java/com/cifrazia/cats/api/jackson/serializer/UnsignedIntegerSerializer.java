package com.cifrazia.cats.api.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.primitives.UnsignedInteger;

import java.io.IOException;

public class UnsignedIntegerSerializer extends JsonSerializer<UnsignedInteger> {
    @Override
    public void serialize(UnsignedInteger unsignedInteger, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(unsignedInteger.longValue());
    }
}

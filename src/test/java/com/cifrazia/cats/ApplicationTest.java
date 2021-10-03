package com.cifrazia.cats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;

public class ApplicationTest {
    public static void main(String[] args) throws JsonProcessingException {
        CatsConnect catsConnect = CatsConnect.builder()
                .apiVersion(UnsignedInteger.ZERO)
                .secretKey("secret_key")
                .ip("185.137.235.92")
                .port(9095)
                .build();
    }
}

package com.cifrazia.cats.model.statement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatementResponse {
    @JsonProperty("server_time")
    private long serverTime;
}

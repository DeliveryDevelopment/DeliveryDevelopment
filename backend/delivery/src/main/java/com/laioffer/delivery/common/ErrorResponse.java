package com.laioffer.delivery.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("error")
    private final String error;

    @JsonProperty("message")
    private final String message;
}

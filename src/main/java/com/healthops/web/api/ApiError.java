package com.healthops.web.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(int status, String error, Map<String, String> fieldErrors) {

    public static ApiError of(int status, String error) {
        return new ApiError(status, error, Map.of());
    }
}

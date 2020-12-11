package com.tja.bh.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class GenericResponse<T> implements Serializable {
    @Builder.Default
    private final Status status = Status.ERROR;
    private final String error;
    private final T body;

    public static <T> GenericResponse<T> success(T body) {
        return GenericResponse.<T>builder()
                .status(Status.OK)
                .body(body)
                .build();
    }

    public static <T> GenericResponse<T> error(String errorFormat, Object... params) {
        return error(String.format(errorFormat, params));
    }

    public static <T> GenericResponse<T> error(String error) {
        return GenericResponse.<T>builder()
                .error(error)
                .build();
    }

    public enum Status {
        OK, ERROR
    }
}
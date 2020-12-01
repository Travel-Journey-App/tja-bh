package com.tja.bh.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class GenericResponse<T> implements Serializable {
    @Builder.Default
    private final Status status = Status.ERROR;
    private final T body;

    public static <T> GenericResponse<T> success(T body) {
        return GenericResponse.<T>builder()
                .status(Status.OK)
                .body(body)
                .build();
    }

    public static <T> GenericResponse<T> error() {
        return GenericResponse.<T>builder().build();
    }

    public enum Status {
        OK, ERROR
    }
}
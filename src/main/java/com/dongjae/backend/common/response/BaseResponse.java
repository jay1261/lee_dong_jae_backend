package com.dongjae.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private Integer httpCode;
    private String message;
    private T data;
}

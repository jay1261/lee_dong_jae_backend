package com.dongjae.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private Integer httpCode;
    private String errorCode;
    private String message;
}

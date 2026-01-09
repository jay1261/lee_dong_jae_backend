package com.dongjae.backend.common.exception;

import com.dongjae.backend.common.enums.ErrorType;
import com.dongjae.backend.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse response =
                new ErrorResponse(ex.getErrorType().getHttpStatus().value(),
                        ex.getErrorType().name(),
                        ex.getErrorType().getMessage());
        return ResponseEntity.status(ex.getErrorType().getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse response =
                new ErrorResponse(ErrorType.INTERNAL_SERVER_ERROR.getHttpStatus().value(),
                        ErrorType.INTERNAL_SERVER_ERROR.name(),
                        ErrorType.INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity.status(ErrorType.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String code = ex.getBindingResult().getFieldError().getDefaultMessage();
        ErrorType errorType;

        try {
            errorType = ErrorType.valueOf(code); // enum으로 변환
        } catch (IllegalArgumentException e) {
            errorType = ErrorType.INTERNAL_SERVER_ERROR;
        }

        ErrorResponse response = new ErrorResponse(
                errorType.getHttpStatus().value(),
                errorType.name(),
                errorType.getMessage()
        );

        return ResponseEntity.status(errorType.getHttpStatus()).body(response);
    }
}

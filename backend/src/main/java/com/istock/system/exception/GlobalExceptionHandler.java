package com.istock.system.exception;

import com.istock.system.dtos.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExist.class)
    public ResponseEntity<String> handleUserAlreadyExist(UserAlreadyExist ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(PastilleAlreadyExists.class)
    public ResponseEntity<ApiError> handlePastilleAlreadyExists(PastilleAlreadyExists ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "PASTILLE_ALREADY_EXISTS",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ManufacturerNotFound.class)
    public ResponseEntity<ApiError> handleManufacturerNotFound(ManufacturerNotFound ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "MANUFACTURER_NOT_FOUND",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        String message = "Invalid value for parameter '" + ex.getName() + "'.";

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_QUERY_PARAMETER",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}

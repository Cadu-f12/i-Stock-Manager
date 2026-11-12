package com.istock.system.exception;

import com.istock.system.dtos.response.ApiError;
import com.istock.system.dtos.response.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> externalNames = Map.of(
                "skuCode", "sku_code",
                "minimumStockLevel", "minimum_stock_level",
                "manufacturerId", "manufacturer_id",
                "currentBalance", "current_balance"
        );
        List<FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldError(
                        externalNames.getOrDefault(fieldError.getField(), fieldError.getField()),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Request validation failed.",
                fields
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserAlreadyExist.class)
    public ResponseEntity<ApiError> handleUserAlreadyExist(UserAlreadyExist ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "USER_ALREADY_EXISTS",
                ex.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
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

    @ExceptionHandler(PastilleNotFound.class)
    public ResponseEntity<ApiError> handlePastilleNotFound(PastilleNotFound ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "PASTILLE_NOT_FOUND",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        String message = "Invalid value for parameter '" + ex.getName() + "'.";

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_QUERY_PARAMETER",
                message,
                List.of()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}

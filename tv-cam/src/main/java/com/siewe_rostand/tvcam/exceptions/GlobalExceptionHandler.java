package com.siewe_rostand.tvcam.exceptions;

import com.siewe_rostand.tvcam.shared.Exceptions.EntityAlreadyExistException;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.Exceptions.OperationNotPermittedException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.model.ExceptionResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.siewe_rostand.tvcam.shared.model.BusinessErrorCodes.ACCOUNT_DISABLED;
import static com.siewe_rostand.tvcam.shared.model.BusinessErrorCodes.ACCOUNT_LOCKED;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

/**
 * @author rostand
 * @project tv-cam
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectValidationException.class)
    public  ResponseEntity<HttpResponse> handle(ObjectValidationException exception) {
          log.trace(exception.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        HttpResponse.builder()
                                .timestamp(now())
                                .statusCode(BAD_REQUEST.value())
                                .status(BAD_REQUEST)
                                .reason(exception.getReason())
                                .developerMessage(exception.getViolations().toString())
                                .errorSource(exception.getViolationSource())
                                .build()
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public  ResponseEntity<HttpResponse> handle(ConstraintViolationException exception) {
          log.trace(exception.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        HttpResponse.builder()
                                .timestamp(now())
                                .statusCode(BAD_REQUEST.value())
                                .status(BAD_REQUEST)
                                .reason("Validation error")
                                .developerMessage(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EmptyPasswordException.class)
    public ResponseEntity<ExceptionResponse> handleException(EmptyPasswordException exp) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .statusCode(BAD_REQUEST.value())
                        .status(BAD_REQUEST.getReasonPhrase())
                        .error(exp.getMessage()).build()
                );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .statusCode(ACCOUNT_LOCKED.getCode())
                        .status(ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage()).build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .statusCode(ACCOUNT_DISABLED.getCode())
                                .status(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> handleException(BadCredentialsException exception) {
        log.trace(exception.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        HttpResponse.builder()
                                .timestamp(now())
                                .statusCode(BAD_REQUEST.value())
                                .status(BAD_REQUEST)
                                .reason("phone number and / or Password is incorrect")
                                .developerMessage(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<HttpResponse> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.trace(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason(exception.getMessage().contains("Duplicate entry") ? "Information already exists" : exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<HttpResponse> handleException(EntityAlreadyExistException exp) {
        log.trace(exp.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason("You Have some Duplicate Entry")
                        .developerMessage(exp.getMessage())
                        .status(CONFLICT)
                        .statusCode(CONFLICT.value())
                        .build(), CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exp) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .statusCode(NOT_FOUND.value())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> handleException(Exception exp) {
        log.trace(exp.getMessage());
        return  new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason("Internal error, please contact the admin")
                        .developerMessage(exp.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<HttpResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason("The requested method is not allowed for this endpoint")
                        .developerMessage(ex.getMessage())
                        .status(METHOD_NOT_ALLOWED)
                        .statusCode(METHOD_NOT_ALLOWED.value())
                        .build(), METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason("Access denied. You don't have access")
                        .developerMessage(exception.getMessage())
                        .status(FORBIDDEN)
                        .statusCode(FORBIDDEN.value())
                        .build(), FORBIDDEN);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HttpResponse> processRuntimeException(RuntimeException exception) {
        exception.printStackTrace();
        log.trace(Arrays.toString(exception.getStackTrace()));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason("An internal server error occurred.")
                        .developerMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build(), INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> processRuntimeException(ApiException exception) {
        exception.printStackTrace();
        log.trace(Arrays.toString(exception.getStackTrace()));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason(exception.getMessage().contains("Duplicate entry") ? "Information already exists" : exception.reason)
                        .developerMessage(exception.fillInStackTrace().toString())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HttpResponse> processRuntimeException(ResourceNotFoundException exception) {
        log.trace(Arrays.toString(exception.getStackTrace()));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timestamp(now())
                        .reason("This resource has not been found")
                        .developerMessage(exception.getMessage())
                        .status(NOT_FOUND)
                        .statusCode(NOT_FOUND.value())
                        .build(), NOT_FOUND);
    }



}

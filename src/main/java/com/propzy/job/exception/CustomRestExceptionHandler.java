package com.propzy.job.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.quartz.SchedulerException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.propzy.core.common.exception.BaseException;
import com.propzy.core.common.exception.PropagationErrorCode;
import com.propzy.job.dto.ApiResponse;
import com.propzy.job.dto.ApiResponseEntity;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("handleNoHandlerFoundException occurred! ", ex);
        String msg = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        Map<String, Object> error = new HashMap<>();
        error.put("urlNotFound", msg);
        return ApiResponseEntity.failure(HttpStatus.NOT_FOUND, msg, error);
    }

    // 413 MultipartException - file size too big
    @ExceptionHandler({MultipartException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleSizeExceededException(
            final WebRequest request, final MultipartException ex) {
        log.error("handleSizeExceededException occurred!", ex);
        String msg = "File exceeds its maximum permitted size";
        Map<String, Object> error = new HashMap<>();
        error.put("sizeExceeded", msg);
        return ApiResponseEntity.failure(
                HttpStatus.PAYLOAD_TOO_LARGE, HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(), error);
    }

    @Override
    protected ResponseEntity handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("handleMissingPathVariable occurred!");
        ex.printStackTrace();
        Map<String, Object> error = new HashMap<>();
        error.put(ex.getVariableName(), ex.getMessage());

        return ApiResponseEntity.failure(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), error);
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.warn("handleMethodArgumentNotValid occurred!", ex);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> fieldErrs = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            fieldErrs.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        for (ObjectError objectError : globalErrors) {
            response.put(objectError.getObjectName(), objectError.getDefaultMessage());
        }
        response.put("fieldErrors", fieldErrs);
        return ApiResponseEntity.failure(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), response);
    }

    @Override
    protected ResponseEntity handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.error("handleMissingServletRequestParameter occurred!", ex);
        Map<String, Object> error = new HashMap<>();
        error.put(ex.getParameterName(), ex.getMessage());

        return ApiResponseEntity.failure(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), error);
    }

    @Override
    protected ResponseEntity handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.error("handleHttpMediaTypeNotSupported occurred!", ex);
        Map<String, Object> error = new HashMap<>();
        error.put("mediaTypeNotSupport", ex.getMessage());

        return ApiResponseEntity.failure(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.BAD_REQUEST.getReasonPhrase(), error);
    }

    @Override
    protected ResponseEntity handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ApiResponseEntity.failure(IdErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ApiResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        ex.printStackTrace();
        Map<String, Object> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(
                    violation.getPropertyPath().toString(),
                    violation.getPropertyPath() + " | " + violation.getMessage());
        }
        return ApiResponseEntity.failure(
                HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), errors);
    }

    @ExceptionHandler({BaseException.class})
    public ApiResponseEntity<Object> handleAllExceptions(BaseException ex) {
        log.error("{}", ex.getMessage(), ex);
        return ApiResponseEntity.failure(ex.getErrorCode());
    }

    @ExceptionHandler({CustomResourceNotFoundException.class})
    public ApiResponseEntity<Object> handleCustomResourceNotFoundException(CustomResourceNotFoundException ex) {
        log.error("{}", ex.getMessage() + ex.getMessageParam(), ex);
        return ApiResponseEntity.failure(ex.getErrorCode(), ex.getMessageParam());
    }

    @ExceptionHandler({CustomResourceDuplicatedException.class})
    public ApiResponseEntity<Object> handleCustomResourceDuplicatedException(CustomResourceDuplicatedException ex) {
        log.error("{}", ex.getMessage() + ex.getMessageParam(), ex);
        return ApiResponseEntity.failure(ex.getErrorCode(), ex.getMessageParam());
    }

    @ExceptionHandler({ServiceException.class})
    public ApiResponseEntity<Object> handleServiceException(ServiceException ex) {
        log.error("{}", ex.getCustomMessage(), ex);
        return ApiResponseEntity.failure(ex.getCustomMessage());
    }

    @ExceptionHandler({CustomInvalidParameterException.class})
    public ApiResponseEntity<Object> handleCustomInvalidParameterException(CustomInvalidParameterException ex) {
        log.error("{}", ex.getMessage() + ex.getMessageParam(), ex);
        return ApiResponseEntity.failure(ex.getErrorCode(), ex.getMessageParam());
    }

    @ExceptionHandler({SchedulerException.class})
    public ApiResponseEntity<Object> handleSchedulerException(SchedulerException ex) {
        log.error("{}", ex.getMessage(), ex);
        return ApiResponseEntity.failure(IdErrorCode.QUARTZ_ERROR);
    }

    @Override
    protected ResponseEntity handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("{}", ex.getMessage(), ex);
        return ApiResponseEntity.failure(IdErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.warn("{}", ex.getMessage());
        final var cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            final var errorField =
                    ((InvalidFormatException) cause)
                            .getPath().stream().map(JsonMappingException.Reference::getFieldName).findFirst();
            if (errorField.isPresent()) {
                return ApiResponseEntity.failure(
                        new PropagationErrorCode(
                                IdErrorCode.INVALID_REQUEST.getErrorCode(),
                                String.format("Invalid %s", errorField.get()),
                                IdErrorCode.INVALID_REQUEST.getHttpStatusCode(),
                                null));
            }
        }

        return ApiResponseEntity.failure(IdErrorCode.MESSAGE_NOT_READABLE);
    }

    @Override
    protected ResponseEntity handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("{}", ex.getMessage());
        return ApiResponseEntity.failure(IdErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponseEntity<Object> handleAllRuntimeException(Exception ex) {
        log.error("{}", ex.getMessage(), ex);
        return ApiResponseEntity.failure(IdErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(feign.RetryableException.class)
    public ApiResponseEntity<Object> handleRetryableException(
            feign.RetryableException ex, WebRequest request) {
        log.warn("Service unavailable {}", ex.getMessage());
        return ApiResponseEntity.failure(IdErrorCode.SERVICE_UNAVAILABLE);
    }
}

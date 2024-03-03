package com.propzy.job.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseEntity<T> extends ResponseEntity<ApiResponse<T>> {
  private ApiResponseEntity(ApiResponse<T> body, HttpStatus status) {
    super(body, status);
  }

  public static <T> ApiResponseEntity<T> success(HttpStatus status, String message, T body) {
    final ApiResponse<T> res = new ApiResponse<>(status, message, body);
    return new ApiResponseEntity<>(res, status);
  }

  public static ApiResponseEntity<String> success() {
    final var ok = HttpStatus.OK;
    final ApiResponse<String> res =
        new ApiResponse<>(ok, ok.getReasonPhrase(), ok.getReasonPhrase());
    return new ApiResponseEntity<>(res, ok);
  }

  public static ApiResponseEntity<String> acceptedRequest() {
    final var ok = HttpStatus.ACCEPTED;
    final ApiResponse<String> res =
        new ApiResponse<>(ok, ok.getReasonPhrase(), ok.getReasonPhrase());
    return new ApiResponseEntity<>(res, ok);
  }

  public static <T> ApiResponseEntity<T> success(T body) {
    final ApiResponse<T> res =
        new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), body);
    return new ApiResponseEntity<>(res, HttpStatus.OK);
  }

  public static ApiResponseEntity<Object> failure(ErrorCode errorCode) {
    final ApiResponse<Object> res =
        new ApiResponse<>(
            errorCode.getErrorCode(), errorCode.getErrorMessage(), errorCode.getData());
    return new ApiResponseEntity<>(res, HttpStatus.valueOf(errorCode.getHttpStatusCode()));
  }

  public static ApiResponseEntity<Object> failure(ErrorCode errorCode, String messageParam) {
    final ApiResponse<Object> res =
        new ApiResponse<>(
            errorCode.getErrorCode(), errorCode.getErrorMessage() + messageParam, errorCode.getData());
    return new ApiResponseEntity<>(res, HttpStatus.valueOf(errorCode.getHttpStatusCode()));
  }

  public static ApiResponseEntity<Object> failure(String message) {
    final ApiResponse<Object> res =
        new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    return new ApiResponseEntity<>(res, HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  public static <T> ApiResponseEntity<T> failure(HttpStatus httpStatus, String message, T result) {
    final ApiResponse<T> res = new ApiResponse<>(httpStatus, message, result);
    return new ApiResponseEntity<>(res, httpStatus);
  }
}

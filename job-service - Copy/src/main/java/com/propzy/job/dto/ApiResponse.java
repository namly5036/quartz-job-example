package com.propzy.job.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

  private int code;
  private String message;
  private T data;

  public ApiResponse(T result) {
    this.code = HttpStatus.OK.value();
    this.message = "SUCCESS";
    this.data = result;
  }

  public ApiResponse(HttpStatus code, String message, T result) {
    this.code = code.value();
    this.message = message;
    this.data = result;
  }

  public ApiResponse(HttpStatus code, String message) {
    this.code = code.value();
    this.message = message;
  }

  public ApiResponse(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }
}

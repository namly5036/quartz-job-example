package com.propzy.job.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorType {
  UNAUTHORIZED(401, "service.exception.unauthorized"),
  INTERNAL_SERVER_ERROR(500, "service.exception.internal"),
  BAD_REQUEST(400, "Bad Request");

  private int code;
  private String message;
}

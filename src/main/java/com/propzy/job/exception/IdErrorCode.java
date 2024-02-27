package com.propzy.job.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.propzy.core.common.exception.ErrorCode;

import lombok.Getter;

public enum IdErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", 500),
    DATA_NOTFOUND(404, "Data not found for ", 404),
    PERMISSION_DENIED(60001, "Permission denied", 403),
    SERVICE_UNAVAILABLE(60002, "Service unavailable", 503),
    INVALID_TOKEN(60003, "Invalid token or token had expired!", 401),
    INVALID_REQUEST(60004, "Invalid request for ", 400),
    MESSAGE_NOT_READABLE(60005, "Malformed JSON request", 400),
    QUARTZ_ERROR(60006, "Quartz error", 500),
    CANNOT_GET_ACCESS_TOKEN(60007, "Cannot get Access token, value is empty", 500),
    CANNOT_GET_CLIENT_SECRET(60008, "Cannot get Access token, value is empty", 500),
    RESOURCE_DUPLICATE(60009, "Resource duplicate for ", 409),
    METHOD_NOT_ALLOWED(60010, "Method now allowed", 400);


    private static final Map<Integer, ErrorCode> errors;

    static {
        errors = Arrays.stream(values()).collect(Collectors.toMap(IdErrorCode::getErrorCode, e -> e));
    }

    @Getter
    private final int errorCode;
    @Getter
    private final String errorMessage;
    @Getter
    private final int httpStatusCode;

    IdErrorCode(int errorCode, String errorMessage, int httpStatusCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatusCode = httpStatusCode;
    }

    public static ErrorCode resolve(int errorCode) {
        return errors.get(errorCode);
    }
}

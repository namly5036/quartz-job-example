package com.propzy.job.dto;

public interface ErrorCode {
    int getErrorCode();

    String getErrorMessage();

    int getHttpStatusCode();

    default Object getData() {
        return null;
    }
}

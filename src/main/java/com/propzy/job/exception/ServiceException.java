package com.propzy.job.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceException extends RuntimeException {
    String customMessage;

    public ServiceException(String customMessage) {
        super(customMessage);
        this.customMessage = customMessage;
    }
}

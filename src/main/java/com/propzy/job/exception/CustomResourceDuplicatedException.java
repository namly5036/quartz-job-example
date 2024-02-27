package com.propzy.job.exception;

import com.propzy.core.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomResourceDuplicatedException extends RuntimeException {
    String messageParam;
    protected final transient ErrorCode errorCode;

    public CustomResourceDuplicatedException(ErrorCode errorCode, String messageParam) {
        super(errorCode.getErrorMessage() + messageParam);
        this.errorCode = errorCode;
        this.messageParam = messageParam;
    }
}

package com.propzy.job.dto;

import com.propzy.job.constant.enums.ExecutionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobEndpointClientResponse<T> {
    ExecutionStatus status;
    String errorMessage;
    private T data;
}

package com.propzy.job.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.job.constant.enums.ExecutionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExecutionHistoryRequest {
    private UUID clientUuid;
    private String signature;
    UUID jobUuid;
    Date executionTimeFrom;
    Date executionTimeTo;
    ExecutionStatus executionStatus;
    String errorMessage;
}

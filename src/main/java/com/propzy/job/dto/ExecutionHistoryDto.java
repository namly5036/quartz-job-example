package com.propzy.job.dto;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.job.constant.enums.ExecutionStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExecutionHistoryDto {
    UUID uuid;
    UUID jobUuid;
    UUID clientUuid;
    Date executionDate;
    ExecutionStatus executionStatus;
    String errorMessage;
    Date createdAt;
    Date updatedAt;
}

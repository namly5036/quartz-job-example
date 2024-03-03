package com.propzy.job.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.job.constant.JobType;
import com.propzy.job.constant.enums.JobStatus;

import com.propzy.job.dto.EndpointApi;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobDetailResponse {
    String uuid;
    String code;
    String name;
    String description;
    String className;
    JobType type;
    String clientUuid;
    String cronExpression;
    String cronDescription;
    Date startTime;
    Date endTime;
    String repeatCount;
    String repeatInterval;
    String timesTriggered;
    Date lastExecution;
    Date nextExecution;
    EndpointApi endpoint;
    Date createdAt;
    String createdBy;
    Date updatedAt;
    String updatedBy;
    JobStatus status;
    String statusDetail; //Quartz status from qrtz_triggers if status = RUNNING
}

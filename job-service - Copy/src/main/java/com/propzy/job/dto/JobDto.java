package com.propzy.job.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.job.constant.JobType;
import com.propzy.job.constant.enums.JobStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobDto {
    String uuid;
    String code;
    String name;
    String description;
    String jobClass;
    JobType jobType;
    String jobGroup;
    String cronExpression;
    String cronDescription;
    Date startTime;
    Date endTime;
    JobStatus status;
    String statusDetail;
}

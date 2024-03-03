package com.propzy.job.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.job.constant.JobType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobListResponse {
    JobKey jobKey;
    String jobName;
    String fullName;
    String jobGroup;
    String description;
    String jobClass;
    boolean isDurable;
    JobDataMap jobDataMap;
}

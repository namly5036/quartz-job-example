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
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobDetailResponse1 {
    String status;
    JobKey jobKey;
    String jobName;
    String fullName;
    String jobGroup;
    String description;
    String jobClass;
    boolean isDurable;
    JobType jobType;
    String cronExpression;
    String cronDescription;
    Date startTime;
    Date endTime;
    long repeatCount;
    long repeatInterval;
    long timesTriggered;
    Date prevExecution;
    Date nextExecution;
    Date lastExecution;
    TimeZone timeZone;
    JobDataMap jobDataMap;

    List<String> jobGroupNames;
    List<JobExecutionContext> currentlyExecutingJobs;
//    Set<JobKey> jobKeys;
}

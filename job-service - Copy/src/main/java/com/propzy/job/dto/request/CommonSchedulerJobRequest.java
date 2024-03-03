package com.propzy.job.dto.request;

import com.propzy.job.constant.JobType;
import com.propzy.job.dto.EndpointApi;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CommonSchedulerJobRequest {
    private UUID jobId;
    private String jobName;
    private String jobGroup;
    private String description;
    private JobType jobType;
    private String cronExpression;
    private String cronDescription;
    private Date startTime;
    private Date endTime;
    private Integer repeatCount;
    private Integer repeatInterval;
    private EndpointApi endpoint;
}

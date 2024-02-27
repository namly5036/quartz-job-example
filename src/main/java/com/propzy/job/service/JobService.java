package com.propzy.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.core.common.job.dto.request.CommonChangeJobRequest;
import com.propzy.core.common.job.dto.request.CommonSchedulerJobRequest;
import com.propzy.job.dto.request.FilterJobRequest;
import com.propzy.job.dto.response.JobDetailResponse;
import com.propzy.job.dto.response.JobFilterResponse;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.util.UUID;

public interface JobService {
    JobDetailResponse getDetail(CommonChangeJobRequest request) throws JsonProcessingException;
    UUID create(CommonSchedulerJobRequest request) throws SchedulerException, ClassNotFoundException, JsonProcessingException;
    void update(CommonSchedulerJobRequest request) throws JsonProcessingException, SchedulerException;
    void executeJobBusiness(JobExecutionContext jobExecutionContext);
    void delete(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;
    void pause(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;
    void resume(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;
    JobFilterResponse filter(FilterJobRequest request, int page, int size) throws JsonProcessingException;
}

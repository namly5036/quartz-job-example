package com.propzy.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.job.dto.request.CommonChangeJobRequest;
import com.propzy.job.dto.request.CommonChangeJobRequest1;
import com.propzy.job.dto.request.CommonSchedulerJobRequest;
import com.propzy.job.dto.request.FilterJobRequest;
import com.propzy.job.dto.response.JobDetailResponse;
import com.propzy.job.dto.response.JobDetailResponse1;
import com.propzy.job.dto.response.JobFilterResponse;
import com.propzy.job.dto.response.JobListResponse;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.UUID;

public interface JobService {
    JobDetailResponse getDetail(CommonChangeJobRequest request) throws JsonProcessingException;
    List<JobListResponse> list() throws SchedulerException;
    JobDetailResponse1 getDetail1(CommonChangeJobRequest1 request) throws JsonProcessingException;
    UUID create(CommonSchedulerJobRequest request) throws SchedulerException, ClassNotFoundException, JsonProcessingException;
    void update(CommonSchedulerJobRequest request) throws JsonProcessingException, SchedulerException;
    void executeJobBusiness(JobExecutionContext jobExecutionContext);
    void delete(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;
    void pause(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;
    void resume(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;
    JobFilterResponse filter(FilterJobRequest request, int page, int size) throws JsonProcessingException;
}

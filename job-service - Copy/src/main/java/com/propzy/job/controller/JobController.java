package com.propzy.job.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.job.dto.request.*;
import com.propzy.job.controller.api.JobApi;
import com.propzy.job.dto.ApiResponseEntity;
import com.propzy.job.dto.response.*;
import com.propzy.job.service.JobExecutionHistoryService;
import com.propzy.job.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobController implements JobApi {
    JobService jobService;
    JobExecutionHistoryService execHistoryService;

    @Override
    public ApiResponseEntity<JobDetailResponse> getDetailJob(CommonChangeJobRequest request) throws JsonProcessingException {
        return ApiResponseEntity.success(jobService.getDetail(request));
    }

    @Override
    public ApiResponseEntity<JobDetailResponse1> getDetailJob1(CommonChangeJobRequest1 request) throws JsonProcessingException {
        return ApiResponseEntity.success(jobService.getDetail1(request));
    }

    @Override
    public ApiResponseEntity<List<JobListResponse>> list() throws SchedulerException {
        return ApiResponseEntity.success(jobService.list());
    }

    @Override
    public ApiResponseEntity<UUID> createJob(CommonSchedulerJobRequest request)
            throws SchedulerException, ClassNotFoundException, JsonProcessingException {
        return ApiResponseEntity.success(jobService.create(request));
    }

    @Override
    public ApiResponseEntity<String> receiveCallBack(ReceiveCallbackRequest request) {
        execHistoryService.updateJobExecutionHistory(
                request.getSpanId().toString(), request.getStatus(), request.getErrorMessage());
        return ApiResponseEntity.success();
    }

    @Override
    public ApiResponseEntity<String> update(CommonSchedulerJobRequest request) throws JsonProcessingException, SchedulerException {
        this.jobService.update(request);
        return ApiResponseEntity.success();
    }

    @Override
    public ApiResponseEntity<String> endpointPlaceholder(Map<String, Object> request) {
        //When Feign cannot call to Endpoint
        return ApiResponseEntity.success();
    }

    @Override
    public ApiResponseEntity<String> deleteJob(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException {
        jobService.delete(request);
        return ApiResponseEntity.success();
    }

    @Override
    public ApiResponseEntity<String> pauseJob(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException {
        jobService.pause(request);
        return ApiResponseEntity.success();
    }

    @Override
    public ApiResponseEntity<String> resumeJob(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException {
        jobService.resume(request);
        return ApiResponseEntity.success();
    }

    @Override
    public ApiResponseEntity<JobFilterResponse> filterJobs(FilterJobRequest request, int page, int size) throws JsonProcessingException {
        return ApiResponseEntity.success(jobService.filter(request, page, size));
    }

    @Override
    public ApiResponseEntity<JobExecutionResponse> filterExecutionHistory(ExecutionHistoryRequest request, int page, int size) {
        return ApiResponseEntity.success(execHistoryService.filter(request, page, size));
    }
}

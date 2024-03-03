package com.propzy.job.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.job.dto.ApiResponseEntity;
import com.propzy.job.dto.request.*;
import com.propzy.job.dto.response.*;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/job")
public interface JobApi {
    @PostMapping(value = "/detail")
    ApiResponseEntity<JobDetailResponse> getDetailJob(@RequestBody CommonChangeJobRequest request) throws JsonProcessingException;

    @PostMapping(value = "/detail1")
    ApiResponseEntity<JobDetailResponse1> getDetailJob1(@RequestBody CommonChangeJobRequest1 request) throws JsonProcessingException;

    @PostMapping(value = "/list")
    ApiResponseEntity<List<JobListResponse>> list() throws JsonProcessingException, SchedulerException;

    @PostMapping(value = "/create")
    ApiResponseEntity<UUID> createJob(@RequestBody CommonSchedulerJobRequest request) throws SchedulerException, ClassNotFoundException, JsonProcessingException;

    @PostMapping(value = "/receive-call-back")
    ApiResponseEntity<String> receiveCallBack(@RequestBody ReceiveCallbackRequest request);

    @PutMapping(value = "/update")
    ApiResponseEntity<String> update(@RequestBody CommonSchedulerJobRequest request) throws JsonProcessingException, SchedulerException;

    @PostMapping(value = "/endpoint-placeholder")
    ApiResponseEntity<String> endpointPlaceholder(@RequestBody Map<String, Object> request);

    @DeleteMapping(value = "/delete")
    ApiResponseEntity<String> deleteJob(@RequestBody CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;

    @PutMapping(value = "/pause")
    ApiResponseEntity<String> pauseJob(@RequestBody CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;

    @PutMapping(value = "/resume")
    ApiResponseEntity<String> resumeJob(@RequestBody CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;

    @PostMapping(value = "/filter/{page}/{size}")
    ApiResponseEntity<JobFilterResponse> filterJobs(@RequestBody FilterJobRequest request,
                                                    @PathVariable int page, @PathVariable int size) throws JsonProcessingException;

    @PostMapping(value = "/execution-history/filter/{page}/{size}")
    ApiResponseEntity<JobExecutionResponse> filterExecutionHistory(@RequestBody ExecutionHistoryRequest request,
                                                                   @PathVariable int page, @PathVariable int size);
}

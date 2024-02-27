package com.propzy.job.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.core.common.job.dto.request.CommonChangeJobRequest;
import com.propzy.core.common.job.dto.request.CommonSchedulerJobRequest;
import com.propzy.job.dto.ApiResponseEntity;
import com.propzy.job.dto.request.ExecutionHistoryRequest;
import com.propzy.job.dto.request.FilterJobRequest;
import com.propzy.job.dto.request.ReceiveCallbackRequest;
import com.propzy.job.dto.response.JobDetailResponse;
import com.propzy.job.dto.response.JobExecutionResponse;
import com.propzy.job.dto.response.JobFilterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/job")
@Tag(name = "JobController", description = "Job Management APIs")
public interface JobApi {
    @Operation(summary = "Get Detail Job", description = "Get Detail Job", tags = "JobController")
    @PostMapping(value = "/detail")
    ApiResponseEntity<JobDetailResponse> getDetailJob(@Valid @RequestBody CommonChangeJobRequest request) throws JsonProcessingException;

    @Operation(summary = "Create Job", description = "Create Job", tags = "JobController")
    @PostMapping(value = "/create")
    ApiResponseEntity<UUID> createJob(@Valid @RequestBody CommonSchedulerJobRequest request) throws SchedulerException, ClassNotFoundException, JsonProcessingException;

    @Operation(summary = "Receive call back", description = "Receive call back from domain team", tags = "JobController")
    @PostMapping(value = "/receive-call-back")
    ApiResponseEntity<String> receiveCallBack(@Valid @RequestBody ReceiveCallbackRequest request);

    @Operation(summary = "Update Job", description = "Update Job", tags = "JobController")
    @PutMapping(value = "/update")
    ApiResponseEntity<String> update(@Valid @RequestBody CommonSchedulerJobRequest request) throws JsonProcessingException, SchedulerException;

    @Operation(summary = "Process Default Endpoint Url", description = "Process Default Endpoint Url", tags = "JobController")
    @PostMapping(value = "/endpoint-placeholder")
    ApiResponseEntity<String> endpointPlaceholder(@Valid @RequestBody Map<String, Object> request);

    @Operation(summary = "Delete Job", description = "Delete Job", tags = "JobController")
    @DeleteMapping(value = "/delete")
    ApiResponseEntity<String> deleteJob(@Parameter(required = true) @Valid @RequestBody CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;

    @Operation(summary = "Pause Job", description = "Pause Job", tags = "JobController")
    @PutMapping(value = "/pause")
    ApiResponseEntity<String> pauseJob(@Parameter(required = true) @Valid @RequestBody CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;

    @Operation(summary = "Resume Job", description = "Resume Job", tags = "JobController")
    @PutMapping(value = "/resume")
    ApiResponseEntity<String> resumeJob(@Parameter(required = true) @Valid @RequestBody CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException;

    @Operation(summary = "Filter Jobs", description = "Filter Jobs", tags = "JobController")
    @PostMapping(value = "/filter/{page}/{size}")
    ApiResponseEntity<JobFilterResponse> filterJobs(@Valid @RequestBody FilterJobRequest request,
                                                    @PathVariable int page, @PathVariable int size) throws JsonProcessingException;

    @Operation(summary = "Filter Execution History", description = "Filter Execution History", tags = "JobController")
    @PostMapping(value = "/execution-history/filter/{page}/{size}")
    ApiResponseEntity<JobExecutionResponse> filterExecutionHistory(@Valid @RequestBody ExecutionHistoryRequest request,
                                                                   @PathVariable int page, @PathVariable int size);
}

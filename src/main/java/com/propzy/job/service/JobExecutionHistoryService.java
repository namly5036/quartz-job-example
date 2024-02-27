package com.propzy.job.service;

import com.propzy.job.constant.enums.ExecutionStatus;
import com.propzy.job.dto.request.ExecutionHistoryRequest;
import com.propzy.job.dto.response.JobExecutionResponse;

import java.util.List;

public interface JobExecutionHistoryService {
    String createJobExecutionHistory(String jobUuid);
    void updateJobExecutionHistory(String uuid, ExecutionStatus status, String errorMessage);
    JobExecutionResponse filter(ExecutionHistoryRequest request, int page, int size);
}

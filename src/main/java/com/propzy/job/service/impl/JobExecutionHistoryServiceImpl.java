package com.propzy.job.service.impl;

import com.propzy.core.common.utilities.UuidUtils;
import com.propzy.job.constant.enums.ExecutionStatus;
import com.propzy.job.dto.ExecutionHistoryDto;
import com.propzy.job.dto.request.ExecutionHistoryRequest;
import com.propzy.job.dto.response.JobExecutionResponse;
import com.propzy.job.entity.PzJobExecutionHistory;
import com.propzy.job.exception.CustomResourceNotFoundException;
import com.propzy.job.exception.IdErrorCode;
import com.propzy.job.repository.PzJobExecutionHistoryRepository;
import com.propzy.job.service.JobExecutionHistoryService;
import com.propzy.job.util.JsonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.TimestampType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobExecutionHistoryServiceImpl implements JobExecutionHistoryService {
    PzJobExecutionHistoryRepository executionHistoryRepo;

    @Override
    public String createJobExecutionHistory(String jobUuid) {
        Date currentDate = new Date();
        PzJobExecutionHistory jobExeHistory = new PzJobExecutionHistory();
        jobExeHistory.setUuid(UuidUtils.randomUUID().toString());
        jobExeHistory.setJobUuid(jobUuid);
        jobExeHistory.setExecutionDate(currentDate);
        jobExeHistory.setErrorMessage(null);
        jobExeHistory.setCreatedAt(currentDate);
        jobExeHistory.setUpdatedAt(currentDate);
        executionHistoryRepo.save(jobExeHistory);
        return jobExeHistory.getUuid();
    }

    @Override
    public void updateJobExecutionHistory(String spanId, ExecutionStatus status, String errorMessage) {
        var jobExecHistory = executionHistoryRepo.findFirstByUuid(spanId)
                .orElseThrow(() -> new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "JobExecutionHistory"));
        if (jobExecHistory.getExecutionStatus() != ExecutionStatus.SUCCESS
                && jobExecHistory.getExecutionStatus() != ExecutionStatus.FAILED) {
            jobExecHistory.setExecutionStatus(status);
            jobExecHistory.setErrorMessage(errorMessage);
            jobExecHistory.setUpdatedAt(new Date());
            executionHistoryRepo.save(jobExecHistory);
        }
    }

    @Override
    public JobExecutionResponse filter(ExecutionHistoryRequest request, int page, int size) {
        if (page < 0 || size < 0) {
            return new JobExecutionResponse();
        }
        String executionStatus = null;
        if (request.getExecutionStatus() != null) {
            executionStatus = request.getExecutionStatus().name();
        }
        TypedParameterValue dateFrom = new TypedParameterValue(new TimestampType(), request.getExecutionTimeFrom());
        TypedParameterValue dateTo = new TypedParameterValue(new TimestampType(), request.getExecutionTimeTo());
        String jobUuid = null;
        if (request.getJobUuid() != null) {
            jobUuid = request.getJobUuid().toString();
        }
        String clientUuid = null;
        if (request.getClientUuid() != null) {
            clientUuid = request.getClientUuid().toString();
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        final Page<Map<String, Object>> pageData = executionHistoryRepo.filter(clientUuid,jobUuid, dateFrom, dateTo,
                executionStatus, request.getErrorMessage(), pageable);

        JobExecutionResponse response = new JobExecutionResponse();
        response.setData(JsonUtils.convertListMapToListObject(pageData.getContent(), ExecutionHistoryDto.class));
        response.setTotalItems(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());
        return response;
    }
}

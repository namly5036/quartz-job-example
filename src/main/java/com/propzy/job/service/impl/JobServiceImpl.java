package com.propzy.job.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.core.common.job.constant.enums.JobType;
import com.propzy.core.common.job.dto.EndpointApi;
import com.propzy.core.common.job.dto.request.CommonChangeJobRequest;
import com.propzy.core.common.job.dto.request.CommonSchedulerJobRequest;
import com.propzy.job.constant.enums.ExecutionStatus;
import com.propzy.job.constant.enums.FieldDefine;
import com.propzy.job.constant.enums.JobStatus;
import com.propzy.job.dto.JobDto;
import com.propzy.job.dto.JobEndpointClientResponse;
import com.propzy.job.dto.request.SchedulerJobRequest;
import com.propzy.job.dto.request.FilterJobRequest;
import com.propzy.job.dto.response.JobDetailResponse;
import com.propzy.job.dto.response.JobFilterResponse;
import com.propzy.job.entity.PzJob;
import com.propzy.job.exception.CustomInvalidParameterException;
import com.propzy.job.exception.CustomResourceNotFoundException;
import com.propzy.job.exception.IdErrorCode;
import com.propzy.job.exception.ServiceException;
import com.propzy.job.job.CommonCronJob;
import com.propzy.job.job.CommonSimpleJob;
import com.propzy.job.mapper.SchedulerJobRequestMapper;
import com.propzy.job.repository.PzJobRepository;
import com.propzy.job.service.JobEndpointService;
import com.propzy.job.service.JobExecutionHistoryService;
import com.propzy.job.service.JobService;
import com.propzy.job.service.NotificationChannelService;
import com.propzy.job.service.QuartzJobService;
import com.propzy.job.util.JsonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobServiceImpl implements JobService {
    PzJobRepository pzJobRepo;
    QuartzJobService quartzJobService;
    JobExecutionHistoryService jobExcHistoryService;
    SchedulerFactoryBean schedulerFactoryBean;
    ModelMapper modelMapper;
    NotificationChannelService notiChannelService;
    JobEndpointService jobEndpointService;
    ObjectMapper objectMapper;

    @Override
    public JobDetailResponse getDetail(CommonChangeJobRequest request) throws JsonProcessingException {
        notiChannelService.verifyClient(request);
        Map<String, Object> map = pzJobRepo.getJobDetail(request.getJobUuid().toString());
        if (map.isEmpty()) {
            throw new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "Job");
        }
        JobDetailResponse data = JsonUtils.convertMapToObject(map, JobDetailResponse.class);
        if (data.getStatus() == JobStatus.RUNNING) {
            Optional<String> statusDetailOpt = pzJobRepo.getJobDetailStatus(request.getJobUuid().toString());
            statusDetailOpt.ifPresent(statusDetail -> data.setStatusDetail(statusDetail));
        }
        data.setEndpoint(objectMapper.readValue((String) map.get(FieldDefine.ENDPOINT_API.getFieldName()), EndpointApi.class));
        return data;
    }

    @Override
    public UUID create(CommonSchedulerJobRequest request) throws SchedulerException, ClassNotFoundException, JsonProcessingException {
        if (StringUtils.isEmpty(request.getJobName())) {
            throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "jobName");
        }
        if (request.getEndpoint() == null || StringUtils.isEmpty(request.getEndpoint().getUrl())) {
            throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "endpoint");
        }
        validateTriggerRequest(request);
        notiChannelService.verifyClient(request);

        SchedulerJobRequest schedulerRequest = SchedulerJobRequestMapper.INSTANCE.CommonToSchedulerJobRequest(request);
        PzJob job = new PzJob(schedulerRequest);
        schedulerRequest.setJobUuid(UUID.fromString(job.getUuid()));
        schedulerRequest.setJobCode(job.getCode());
        schedulerRequest.setJobClass(job.getJobClass());
        quartzJobService.scheduleNewJob(schedulerRequest);
        UUID jobUUid = UUID.fromString(pzJobRepo.save(job).getUuid());
        log.info(">>>>> jobName = [" + job.getUuid() + "]" + " scheduled.");
        return jobUUid;
    }

    private void validateTriggerRequest(CommonSchedulerJobRequest request) {
        final Date currentDate = new Date();
        if (request.getStartTime().before(currentDate)) {
            throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "startTime");
        }

        if (request.getEndTime() != null
                && request.getEndTime().before(request.getStartTime())) {
            throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "endTime");
        }

        if (JobType.CRON.equals(request.getJobType())) {
            if (StringUtils.isEmpty(request.getCronExpression())) {
                throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "cronExpression");
            }
        } else {
            if (request.getRepeatCount() != null && request.getRepeatCount() < 0) {
                throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "repeatCount");
            }
            if (request.getRepeatCount() != null && request.getRepeatInterval() != null
                    && request.getRepeatCount() > 0 && request.getRepeatInterval() < 0) {
                throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "repeatInterval");
            }
        }
    }

    @Override
    public void update(CommonSchedulerJobRequest request) throws JsonProcessingException, SchedulerException {
        if (request.getJobUuid() == null) {
            throw new CustomInvalidParameterException(IdErrorCode.DATA_NOTFOUND, "JobUuid");
        }
        validateTriggerRequest(request);
        notiChannelService.verifyClient(request);

        SchedulerJobRequest schedulerRequest = SchedulerJobRequestMapper.INSTANCE.CommonToSchedulerJobRequest(request);
        PzJob job = pzJobRepo.findByUuid(request.getJobUuid().toString()).orElseThrow(
                () -> new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "Job"));
        if (JobStatus.DELETED.equals(job.getStatus())) {
            log.error(">>>>> jobName = [" + job.getUuid() + "]" + " was deleted.");
            throw new ServiceException("Cannot update because Job was deleted");
        }
        String jobCode = request.getJobName().toUpperCase().replace(" ", "_") + "_" + Instant.now().toEpochMilli();
        schedulerRequest.setJobCode(jobCode);
        quartzJobService.updateScheduleJob(schedulerRequest);
        job.setCode(jobCode);
        if (!StringUtils.isEmpty(request.getJobName())) {
            job.setName(request.getJobName().trim());
        }
        if (!StringUtils.isEmpty(request.getDescription())) {
            job.setDescription(request.getDescription().trim());
        }
        job.setJobType(request.getJobType());
        if (JobType.CRON.equals(request.getJobType())) {
            job.setCronExpression(request.getCronExpression().trim());
            job.setCronDescription(request.getCronDescription().trim());
            job.setJobClass(CommonCronJob.class.getName());
        } else {
            job.setJobClass(CommonSimpleJob.class.getName());
            //TODO add repeatCount + repeatInterval
        }
        job.setStartTime(request.getStartTime());
        job.setEndTime(request.getEndTime());
        job.setEndpointApi(request.getEndpoint());
        job.setStatus(JobStatus.RUNNING);
        job.setUpdatedAt(new Date());
        pzJobRepo.save(job);
    }

    @Override
    public void executeJobBusiness(JobExecutionContext jobExecutionContext) {
        String spanId = null;
        try {
            JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
            var jobUuid = jobKey.getName();
            spanId = jobExcHistoryService.createJobExecutionHistory(jobUuid);
            log.info("===== [JOB UUID: " + jobUuid
                    + ", Name: " + jobExecutionContext.getJobDetail().getDescription()
                    + ", Group: " + jobKey.getGroup()
                    + ", Thread Name: " + Thread.currentThread().getName() + "]");
            JobEndpointClientResponse response = this.jobEndpointService.callToJobEndpoint(jobUuid, spanId);
            jobExcHistoryService.updateJobExecutionHistory(spanId, response.getStatus(), response.getErrorMessage());
        } catch (Exception e) {
            log.error("===== [ERROR] execute Job", e);
            try {
                jobExcHistoryService.updateJobExecutionHistory(spanId, ExecutionStatus.FAILED, ExceptionUtils.getStackTrace(e));
            } catch (Exception ee) {
                log.error("===== [ERROR] execute Job", e);
            }
        }
    }

    @Override
    public void delete(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException {
        notiChannelService.verifyClient(request);
        PzJob job = pzJobRepo.findByUuid(request.getJobUuid().toString()).orElseThrow(
                () -> new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "Job"));
        job.setIsDeleted(true);
        job.setDeletedAt(new Date());
        job.setStatus(JobStatus.DELETED);
        pzJobRepo.save(job);
        log.info(">>>>> jobName = [" + job.getName() + "]" + " deleted.");
        schedulerFactoryBean.getScheduler().deleteJob(new JobKey(job.getUuid(), job.getJobGroup()));
    }

    @Override
    public void pause(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException {
        notiChannelService.verifyClient(request);

        var job = pzJobRepo.findByUuid(request.getJobUuid().toString()).orElseThrow(
                () -> new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "Job"));
        if (job.getStatus() != JobStatus.RUNNING) {
            throw new ServiceException("Cannot pause because Job currently is " + job.getStatus());
        }
        job.setStatus(JobStatus.PAUSED);
        job.setUpdatedAt(new Date());
        pzJobRepo.save(job);
        JobKey jobKey = new JobKey(request.getJobUuid().toString(), job.getJobGroup());
        schedulerFactoryBean.getScheduler().pauseJob(jobKey);
    }

    @Override
    public void resume(CommonChangeJobRequest request) throws SchedulerException, JsonProcessingException {
        notiChannelService.verifyClient(request);
        PzJob job = pzJobRepo.findByUuid(request.getJobUuid().toString()).orElseThrow(
                () -> new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "Job"));
        if (job.getStatus() != JobStatus.PAUSED) {
            throw new ServiceException("Cannot resume because Job currently is NOT paused");
        }
        job.setStatus(JobStatus.RUNNING);
        job.setUpdatedAt(new Date());
        pzJobRepo.save(job);
        schedulerFactoryBean.getScheduler().resumeJob(new JobKey(job.getUuid(), job.getJobGroup()));
        log.info(">>>>> jobName = [" + job.getName() + "]" + " resumed.");
    }

    @Override
    public JobFilterResponse filter(FilterJobRequest request, int page, int size) throws JsonProcessingException {
        if (page < 0 || size < 0) {
            return new JobFilterResponse();
        }
        notiChannelService.verifyClient(request);
        String jobType = null;
        if (request.getJobType() != null) {
            jobType = request.getJobType().name();
        }
        String keyGroup = null;
        if (request.getFilterClientUuid() != null) {
            keyGroup = request.getFilterClientUuid().toString();
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        final Page<Map<String, Object>> pageData = pzJobRepo.filterJobs(request.getJobName(), jobType, keyGroup, pageable);
        JobFilterResponse response = new JobFilterResponse();
        response.setData(JsonUtils.convertListMapToListObject(pageData.getContent(), JobDto.class));
        response.setTotalItems(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());
        return response;
    }
}

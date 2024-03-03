package com.propzy.job.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.propzy.job.dto.EndpointApi;
import com.propzy.job.dto.request.*;
import com.propzy.job.constant.JobType;
import com.propzy.job.constant.enums.ExecutionStatus;
import com.propzy.job.constant.enums.FieldDefine;
import com.propzy.job.constant.enums.JobStatus;
import com.propzy.job.dto.JobDto;
import com.propzy.job.dto.JobEndpointClientResponse;
import com.propzy.job.dto.response.JobDetailResponse;
import com.propzy.job.dto.response.JobDetailResponse1;
import com.propzy.job.dto.response.JobFilterResponse;
import com.propzy.job.dto.response.JobListResponse;
import com.propzy.job.entity.PzJob;
import com.propzy.job.job.CommonCronJob;
import com.propzy.job.job.CommonSimpleJob;
import com.propzy.job.repository.PzJobRepository;
import com.propzy.job.service.JobEndpointService;
import com.propzy.job.service.JobExecutionHistoryService;
import com.propzy.job.service.JobService;
import com.propzy.job.service.QuartzJobService;
import com.propzy.job.util.JsonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

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
    JobEndpointService jobEndpointService;
    ObjectMapper objectMapper;

    @Override
    public JobDetailResponse getDetail(CommonChangeJobRequest request) throws JsonProcessingException {
        Map<String, Object> map = pzJobRepo.getJobDetail(request.getJobId().toString());
        JobDetailResponse data = JsonUtils.convertMapToObject(map, JobDetailResponse.class);
        if (data.getStatus() == JobStatus.RUNNING) {
            Optional<String> statusDetailOpt = pzJobRepo.getJobDetailStatus(request.getJobId().toString());
            statusDetailOpt.ifPresent(statusDetail -> data.setStatusDetail(statusDetail));
        }
        data.setEndpoint(objectMapper.readValue((String) map.get(FieldDefine.ENDPOINT_API.getFieldName()), EndpointApi.class));
        return data;
    }

    public List<JobListResponse> list() throws SchedulerException {
        List<JobListResponse> list = new ArrayList<>();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        // Get all job keys for the current group
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
            // Get job details
//                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
            JobListResponse response = new JobListResponse();
            response.setJobKey(jobDetail.getKey());
            response.setJobName(jobDetail.getName());
            response.setFullName(jobDetail.getFullName());
            response.setJobGroup(jobDetail.getGroup());
            response.setDescription(jobDetail.getDescription());
            response.setJobClass(jobDetail.getJobClass().toString());
            response.setDurable(jobDetail.isDurable());
            response.setJobDataMap(jobDetail.getJobDataMap());
            list.add(response);
        }
        return list;
    }

    @SneakyThrows
    @Override
    public JobDetailResponse1 getDetail1(CommonChangeJobRequest1 request) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(request.getJobName(), request.getJobGroup());
        TriggerKey triggerKey = new TriggerKey(request.getJobName(), request.getJobGroup());
        JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
        Trigger trigger = scheduler.getTrigger(triggerKey);
        JobDetailResponse1 response = new JobDetailResponse1();
        response.setJobKey(jobDetail.getKey());
        response.setJobName(jobDetail.getName());
        response.setFullName(jobDetail.getFullName());
        response.setJobGroup(jobDetail.getGroup());
        response.setDescription(jobDetail.getDescription());
        response.setJobClass(jobDetail.getJobClass().toString());
        response.setDurable(jobDetail.isDurable());
        if (trigger instanceof CronTriggerImpl) {
            CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
            response.setCronExpression(cronTrigger.getCronExpression());
            response.setCronDescription(cronTrigger.getExpressionSummary());
            response.setJobType(JobType.CRON);
            response.setTimeZone(cronTrigger.getTimeZone());
        } else if (trigger instanceof SimpleTrigger) {
            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
            response.setJobType(JobType.SIMPLE);
            response.setRepeatCount(simpleTrigger.getRepeatCount());
            response.setRepeatInterval(simpleTrigger.getRepeatInterval());
            response.setTimesTriggered(simpleTrigger.getTimesTriggered());
        }
        response.setStartTime(trigger.getStartTime());
        response.setEndTime(trigger.getEndTime());
        response.setPrevExecution(trigger.getPreviousFireTime());
        response.setNextExecution(trigger.getNextFireTime());
        response.setLastExecution(trigger.getFinalFireTime());
        response.setJobDataMap(jobDetail.getJobDataMap());

        response.setJobGroupNames(scheduler.getJobGroupNames());
        response.setCurrentlyExecutingJobs(scheduler.getCurrentlyExecutingJobs());
//        response.setJobKeys(scheduler.getJobKeys());
        // Get the associated triggers for the job

//        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
//        for (Trigger currentTriggerKey : triggers) {}
        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
//            status = "TriggerState.NORMAL - Not currently running";
        response.setStatus("TriggerState." + triggerState.name());
        return response;
    }

    @Override
    public UUID create(CommonSchedulerJobRequest request) throws SchedulerException, ClassNotFoundException {
        request.setJobId(UUID.randomUUID());
        SchedulerJobRequest schedulerRequest = new SchedulerJobRequest();
        BeanUtils.copyProperties(request, schedulerRequest);
        PzJob job = new PzJob(schedulerRequest);
        schedulerRequest.setDescription(job.getDescription());
        schedulerRequest.setJobClass(job.getJobClass());
        quartzJobService.scheduleNewJob(schedulerRequest);
        log.info(">>>>> jobName = [" + job.getId() + "]" + " scheduled.");
        return job.getId();
    }

    @Override
    public void update(CommonSchedulerJobRequest request) throws SchedulerException {
        SchedulerJobRequest schedulerRequest = new SchedulerJobRequest();
        BeanUtils.copyProperties(request, schedulerRequest);
        PzJob job = pzJobRepo.findById(request.getJobId()).orElseThrow();
        if (JobStatus.DELETED.equals(job.getStatus())) {
            log.error(">>>>> jobName = [" + job.getId() + "]" + " was deleted.");
        }
        quartzJobService.updateScheduleJob(schedulerRequest);
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
            var jobKeyName = jobKey.getName();
            log.info("===== [JOB UUID: " + jobKeyName
                    + ", Name: " + jobExecutionContext.getJobDetail().getDescription()
                    + ", Group: " + jobKey.getGroup()
                    + ", Thread Name: " + Thread.currentThread().getName() + "]");
            spanId = jobExcHistoryService.createJobExecutionHistory(jobKeyName);
            JobEndpointClientResponse response = this.jobEndpointService.callToJobEndpoint(jobKeyName, spanId);
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
    public void delete(CommonChangeJobRequest request) throws SchedulerException {
        PzJob job = pzJobRepo.findById(request.getJobId()).orElseThrow();
        job.setIsDeleted(true);
        job.setDeletedAt(new Date());
        job.setStatus(JobStatus.DELETED);
        pzJobRepo.save(job);
        log.info(">>>>> jobName = [" + job.getName() + "]" + " deleted.");
        schedulerFactoryBean.getScheduler().deleteJob(new JobKey(job.getId().toString(), job.getJobGroup()));
    }

    @Override
    public void pause(CommonChangeJobRequest request) throws SchedulerException {
//        var job = pzJobRepo.findById(request.getJobId()).orElseThrow();
//        if (job.getStatus() != JobStatus.RUNNING) {
//            log.error("Cannot pause because Job currently is " + job.getStatus());
//        }
//        job.setStatus(JobStatus.PAUSED);
//        job.setUpdatedAt(new Date());
//        pzJobRepo.save(job);
//        JobKey jobKey = new JobKey(request.getJobId().toString(), job.getJobGroup());
//        schedulerFactoryBean.getScheduler().pauseJob(jobKey);

//        JobKey jobKey = new JobKey(request.getJobId().toString(), request.get());
//        schedulerFactoryBean.getScheduler().pauseJob(jobKey);
    }

    @Override
    public void resume(CommonChangeJobRequest request) throws SchedulerException {
        PzJob job = pzJobRepo.findById(request.getJobId()).orElseThrow();
        if (job.getStatus() != JobStatus.PAUSED) {
            log.error("Cannot resume because Job currently is NOT paused");
        }
        job.setStatus(JobStatus.RUNNING);
        job.setUpdatedAt(new Date());
        pzJobRepo.save(job);
        schedulerFactoryBean.getScheduler().resumeJob(new JobKey(job.getId().toString(), job.getJobGroup()));
        log.info(">>>>> jobName = [" + job.getName() + "]" + " resumed.");
    }

    @Override
    public JobFilterResponse filter(FilterJobRequest request, int page, int size) {
        if (page < 0 || size < 0) {
            return new JobFilterResponse();
        }
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

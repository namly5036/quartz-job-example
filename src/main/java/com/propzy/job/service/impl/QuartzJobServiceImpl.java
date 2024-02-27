package com.propzy.job.service.impl;

import com.propzy.core.common.job.constant.enums.JobType;
import com.propzy.job.dto.request.SchedulerJobRequest;
import com.propzy.job.exception.CustomResourceDuplicatedException;
import com.propzy.job.exception.IdErrorCode;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.propzy.job.service.QuartzJobService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuartzJobServiceImpl implements QuartzJobService {
    SchedulerFactoryBean schedulerFactoryBean;
    ApplicationContext context;
    JobFactory jobFactory;

    @Override
    public void scheduleNewJob(SchedulerJobRequest request) throws SchedulerException, ClassNotFoundException {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(request.getJobClass()))
                    .withIdentity(request.getJobUuid().toString(), request.getClientUuid().toString())
                    .build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = jobFactory.createJob((Class<? extends QuartzJobBean>) Class.forName(request.getJobClass()), context, request);
                Trigger trigger = null;
                if (JobType.CRON.equals(request.getJobType())) {
                    trigger = jobFactory.createCronTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    //simple job
                    trigger = jobFactory.createSimpleTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }

                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                throw new CustomResourceDuplicatedException(IdErrorCode.RESOURCE_DUPLICATE, "Job");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", request.getJobClass(), e);
            throw new ClassNotFoundException();
        }
    }

    @Override
    public void updateScheduleJob(SchedulerJobRequest request) throws SchedulerException {
        Trigger newTrigger;
        if (JobType.CRON.equals(request.getJobType())) {
            newTrigger = jobFactory.createCronTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
            newTrigger = jobFactory.createSimpleTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        final TriggerKey triggerKey = TriggerKey.triggerKey(request.getJobUuid().toString());
        schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, newTrigger);
        log.info(">>>>> jobName = [" + request.getJobUuid() + "]" + " updated and scheduled.");
    }
}

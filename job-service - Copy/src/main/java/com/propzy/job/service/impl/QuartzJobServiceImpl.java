package com.propzy.job.service.impl;

import com.propzy.job.constant.JobType;
import com.propzy.job.dto.request.SchedulerJobRequest;
import com.propzy.job.job.CommonCronJob;
import com.propzy.job.service.QuartzJobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

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
                    .withIdentity(request.getJobId().toString(), request.getJobName().toString())
                    .build();
            log.info("jobDetail.getKey() = '{}'", jobDetail.getKey());
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = jobFactory.createJob((Class<? extends QuartzJobBean>) Class.forName(request.getJobClass()), context, request);
                Trigger trigger;
                if (JobType.CRON.equals(request.getJobType())) {
                    trigger = jobFactory.createCronTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = jobFactory.createSimpleTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }

                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                log.error("scheduler.checkExists(jobDetail.getKey()");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", request.getJobClass(), e);
            throw new ClassNotFoundException();
        }
    }

//    @Override
//    public void scheduleNewJob(SchedulerJobRequest request) throws SchedulerException, ClassNotFoundException {
//        // create job
//        JobDetail job = JobBuilder.newJob(CommonCronJob.class)
//                .withIdentity("waffleJob", "foodGroup")
//                .storeDurably(true)
//                .requestRecovery(true)
//                .build();
//
//        // create trigger
//        // Build a trigger that will fire every minute starting now
//        CronTrigger trigger = newTrigger()
//                .withIdentity("waffleTrigger", "foodGroup")
//                .withSchedule(cronSchedule("0/10 * * * * ? *"))
//                .build();
//
//        System.out.println(job.getKey());
//        // schedule it
//        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//        scheduler.start();
//        scheduler.scheduleJob(job, trigger);
//    }

    @Override
    public void updateScheduleJob(SchedulerJobRequest request) throws SchedulerException {
        Trigger newTrigger;
        if (JobType.CRON.equals(request.getJobType())) {
            newTrigger = jobFactory.createCronTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
            newTrigger = jobFactory.createSimpleTrigger(request, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        final TriggerKey triggerKey = TriggerKey.triggerKey(request.getJobId().toString());
        schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, newTrigger);
        log.info(">>>>> jobName = [" + request.getJobId() + "]" + " updated and scheduled.");
    }
}

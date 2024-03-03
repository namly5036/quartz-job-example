package com.propzy.job.job;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.propzy.job.service.JobService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@NoArgsConstructor // For QuartzConfig initiate Job instance
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonSimpleJob extends QuartzJobBean implements InterruptableJob {
    @NonFinal
    volatile boolean toStopFlag = true;

    @Autowired
    JobService jobService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("========== [START EXECUTION] SIMPLE JOB  ========== ");
        jobService.executeJobBusiness(jobExecutionContext);
        log.info("========== [END EXECUTION] SIMPLE JOB  ==========");
        log.info("=================================================");
    }

    @Override
    public void interrupt() {
        System.out.println("Stopping thread... ");
        toStopFlag = false;
    }
}

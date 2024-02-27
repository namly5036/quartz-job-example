package com.propzy.job.service;

import com.propzy.job.dto.request.SchedulerJobRequest;
import org.quartz.SchedulerException;

public interface QuartzJobService {
    void scheduleNewJob(SchedulerJobRequest request) throws SchedulerException, ClassNotFoundException;
    void updateScheduleJob(SchedulerJobRequest jobInfo) throws SchedulerException;
}

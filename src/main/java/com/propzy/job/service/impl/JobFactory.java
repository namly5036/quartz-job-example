package com.propzy.job.service.impl;

import com.propzy.job.dto.request.SchedulerJobRequest;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Slf4j
@Component
public class JobFactory {

  public JobDetail createJob(Class<? extends QuartzJobBean> jobClass,
      ApplicationContext context, SchedulerJobRequest request) {
      JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
      factoryBean.setJobClass(jobClass);
      factoryBean.setDescription(request.getJobCode().trim());
      factoryBean.setDurability(true);
      factoryBean.setApplicationContext(context);
      final String jobName = request.getJobUuid().toString();
      factoryBean.setName(jobName);
      factoryBean.setGroup(request.getClientUuid().toString());
      // set job data map
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put(jobName + request.getClientUuid(), request.getJobClass());
      factoryBean.setJobDataMap(jobDataMap);
      factoryBean.afterPropertiesSet();

      return factoryBean.getObject();
  }

  /**
   * Create cron trigger.
   *
   * @param request CreateJobRequest.
   * @param misFireInstruction Misfire instruction (what to do in case of misfire happens).
   * @return Trigger
   */
  public Trigger createCronTrigger(SchedulerJobRequest request, int misFireInstruction) {
      CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
      String triggerName = request.getJobUuid().toString();
      factoryBean.setName(triggerName);
      factoryBean.setDescription(request.getJobCode().trim());
      factoryBean.setStartTime(request.getStartTime());
      factoryBean.setCronExpression(request.getCronExpression());
      factoryBean.setMisfireInstruction(misFireInstruction);
      try {
        factoryBean.afterPropertiesSet();
      } catch (ParseException e) {
          log.error("{}", e.getMessage(), e);
      }

      final CronTriggerImpl cronTrigger = (CronTriggerImpl) factoryBean.getObject();
      if (request.getEndTime() != null) {
          cronTrigger.setEndTime(request.getEndTime());
      }
      return factoryBean.getObject();
  }

  /**
   * Create a Simple trigger.
   *
   * @param request CreateJobRequest.
   * @param misFireInstruction Misfire instruction (what to do in case of misfire happens).
   * @return Trigger
   */
  public Trigger createSimpleTrigger(SchedulerJobRequest request, int misFireInstruction) {
      SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
      String triggerName = request.getJobUuid().toString();
      factoryBean.setName(triggerName);
      factoryBean.setDescription(request.getJobCode().trim());
      factoryBean.setStartTime(request.getStartTime());
      factoryBean.setMisfireInstruction(misFireInstruction);
      if (request.getRepeatCount() != null) {
          factoryBean.setRepeatCount(request.getRepeatCount());
          if (request.getRepeatCount() > 0 && request.getRepeatInterval() != null) {
              factoryBean.setRepeatInterval(request.getRepeatInterval() * 1000);
          }
      }
      factoryBean.afterPropertiesSet();

      final SimpleTriggerImpl simpleTrigger = (SimpleTriggerImpl) factoryBean.getObject();
      if (request.getEndTime() != null) {
          simpleTrigger.setEndTime(request.getEndTime());
      }

      return factoryBean.getObject();
  }
}

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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JobFactory {

  public JobDetail createJob(Class<? extends QuartzJobBean> jobClass, ApplicationContext context, SchedulerJobRequest request) {
      JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
      factoryBean.setJobClass(jobClass);
      factoryBean.setDescription(request.getDescription());
      factoryBean.setDurability(true);
      factoryBean.setApplicationContext(context);
      factoryBean.setName(request.getJobName());
      factoryBean.setGroup(request.getJobGroup());
//      factoryBean.setApplicationContextJobDataKey("applicationContextJobDataKey");
//      factoryBean.setBeanName("beanName1");
//      factoryBean.setRequestsRecovery(false);
      // set job data map
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put("url", "url1");
      jobDataMap.put("component", "component1");
      jobDataMap.put("createBy", "createBy1");
      jobDataMap.put("createDate", "createDate1");
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
//      String triggerName = request.getJobName();

      factoryBean.setName(request.getJobName());
      factoryBean.setDescription(request.getDescription());
      factoryBean.setStartTime(request.getStartTime());
      factoryBean.setCronExpression(request.getCronExpression());
//      factoryBean.setMisfireInstruction(misFireInstruction);
//      factoryBean.setMisfireInstructionName("DEFAULT_PRIORITY");
//      factoryBean.setCalendarName("triggerCalendar1");
//      factoryBean.setBeanName("triggerBean1");
      factoryBean.setGroup(request.getJobGroup());

      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put("trigger1", "Test");
      factoryBean.setJobDataMap(jobDataMap);
//      factoryBean.setJobDetail();
//      factoryBean.setTimeZone();
      factoryBean.setPriority(1);
//      factoryBean.setStartDelay();
//      factoryBean.setStartDelay();
      try {
        factoryBean.afterPropertiesSet();
      } catch (ParseException e) {
          log.error("{}", e.getMessage(), e);
      }

      final CronTriggerImpl cronTrigger = (CronTriggerImpl) factoryBean.getObject();
      if (request.getEndTime() != null) {
          cronTrigger.setEndTime(request.getEndTime());
//          cronTrigger.setNextFireTime();
//          cronTrigger.setPreviousFireTime();
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
      String triggerName = request.getJobName();

      factoryBean.setName(triggerName);
      factoryBean.setDescription(request.getDescription());
      factoryBean.setMisfireInstruction(misFireInstruction);

      factoryBean.setStartTime(request.getStartTime());
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

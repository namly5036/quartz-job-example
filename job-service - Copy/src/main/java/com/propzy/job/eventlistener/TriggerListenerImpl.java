package com.propzy.job.eventlistener;

import com.propzy.job.constant.enums.JobStatus;
import com.propzy.job.repository.PzJobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TriggerListenerImpl implements TriggerListener {
    PzJobRepository pzJobRepo;

    @Override
    public String getName() {
        return "globalTrigger";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        log.info("===== [TriggerListener] - triggerMisfired()");
        String jobUuid = trigger.getJobKey().getName();
        log.info("========== Job Uuid: " + jobUuid + " is misfired");
    }

    @Override
    public void triggerComplete(
            Trigger trigger,
            JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
        if (triggerInstructionCode == CompletedExecutionInstruction.DELETE_TRIGGER) {
            try {
                log.info("===== [TriggerListener] - triggerComplete() - DELETE_TRIGGER");
                UUID jobUuid = UUID.fromString(trigger.getJobKey().getName());
                this.updateJobStatus(jobUuid, JobStatus.COMPLETED);
            } catch (Exception e) {
                log.error("===== [ERROR] TriggerListener.triggerComplete()", e);
            }
        }

    }

    private void updateJobStatus(UUID jobUuid, JobStatus status) {
        var job = pzJobRepo.findById(jobUuid)
                .orElseThrow();
        if (status != job.getStatus()) {
            job.setStatus(status);
            job.setUpdatedAt(new Date());
            pzJobRepo.save(job);
        } else {
            log.warn("===== Job status already updated");
        }
    }
}

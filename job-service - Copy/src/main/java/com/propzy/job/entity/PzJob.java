package com.propzy.job.entity;

import com.propzy.job.constant.JobType;
import com.propzy.job.constant.enums.JobStatus;
import com.propzy.job.dto.EndpointApi;
import com.propzy.job.dto.request.SchedulerJobRequest;
import com.propzy.job.job.CommonCronJob;
import com.propzy.job.job.CommonSimpleJob;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "pz_job")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PzJob extends AuditModel {
    @Id
    UUID id;

    String name;

    String description;

    @Column(name = "class_name")
    String jobClass;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    JobType jobType;

    @Column(name = "client_uuid")
    String jobGroup;

    @Column(name = "cron_expression")
    String cronExpression;

    @Column(name = "cron_description")
    String cronDescription;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    Date endTime;

    @Column(name = "endpoint_api", columnDefinition = "hstore")
    @Type(type = "jsonb")
    EndpointApi endpointApi;

    @Enumerated(EnumType.STRING)
    JobStatus status = JobStatus.RUNNING;


    public PzJob(SchedulerJobRequest request) {
        id = request.getJobId();
        name = request.getJobName().trim();
        description = request.getDescription().trim();
        jobGroup = request.getJobGroup().toString();
        jobType = request.getJobType();
        startTime = request.getStartTime();
        endTime = request.getEndTime();
        if (request.getEndpoint() != null) {
            endpointApi = request.getEndpoint();
        }

        if (JobType.CRON.equals(request.getJobType())) {
            cronExpression = request.getCronExpression().trim();
            cronDescription = request.getCronDescription().trim();
            jobClass = CommonCronJob.class.getName();
        } else {
            jobClass = CommonSimpleJob.class.getName();
        }
        createdAt = new Date();
        updatedAt = createdAt;
        isDeleted = false;
        status = JobStatus.RUNNING;
    }
}

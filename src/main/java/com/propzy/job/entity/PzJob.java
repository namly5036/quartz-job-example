package com.propzy.job.entity;

import com.propzy.core.common.job.constant.enums.JobType;
import com.propzy.core.common.job.dto.EndpointApi;
import com.propzy.core.common.utilities.UuidUtils;
import com.propzy.job.constant.enums.JobStatus;
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

@Data
@Entity
@Table(name = "pz_job")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PzJob extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pz_job_id_generator")
    @SequenceGenerator(name = "pz_job_id_generator", sequenceName = "pz_job_id_seq", allocationSize = 1)
    Long id;

    String uuid;

    String code = "";
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
        name = request.getJobName().trim();
        code = name.toUpperCase().replace(" ", "_") + "_" + Instant.now().toEpochMilli();
        uuid = UuidUtils.randomUUID().toString();
        description = request.getDescription().trim();
        jobGroup = request.getClientUuid().toString();
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

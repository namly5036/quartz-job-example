package com.propzy.job.entity;

import java.util.Date;

import javax.persistence.*;

import com.propzy.job.constant.enums.ExecutionStatus;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "pz_job_execution_history")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
public class PzJobExecutionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pz_job_execution_history_id_generator")
    @SequenceGenerator(name = "pz_job_execution_history_id_generator", sequenceName = "pz_job_execution_history_id_seq", allocationSize = 1)
    Long id;

    String uuid;
    @Column(name = "job_uuid")
    String jobUuid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "execution_date")
    Date executionDate;

    @Column(name = "execution_status")
    @Enumerated(EnumType.STRING)
    ExecutionStatus executionStatus = ExecutionStatus.INIT;

    @Column(name = "error_message")
    String errorMessage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    Date updatedAt;

    public PzJobExecutionHistory(PzJob job) {
    }
}

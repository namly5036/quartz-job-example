package com.propzy.job.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.jpa.TypedParameterValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.propzy.job.entity.PzJob;

@Transactional(readOnly = true)
@Repository
public interface PzJobRepository extends JpaRepository<PzJob, UUID> {
    @Query(value =
                " SELECT j.uuid, j.code, j.name, j.description, j.class_name AS job_class, j.type AS job_type, " +
                    " j.client_uuid AS job_group, j.cron_expression, j.cron_description, j.start_time, j.end_time, " +
                    " j.status, qt.trigger_state AS status_detail " +
                " FROM pz_job j " +
                " LEFT JOIN qrtz_triggers qt ON j.uuid = qt.job_name " +
                " WHERE (:jobName IS NULL OR j.name = cast(:jobName AS VARCHAR)) AND " +
                    " (:jobType IS NULL OR j.type = cast(:jobType AS VARCHAR)) AND " +
                    " (:keyGroup IS NULL or j.client_uuid = cast(:keyGroup AS VARCHAR))  " +
                " ORDER BY created_at DESC ", nativeQuery=true)
    Page<Map<String, Object>> filterJobs(@Param("jobName") String jobName, @Param("jobType") String jobType,
                                               @Param("keyGroup") String keyGroup, Pageable pageable);

    @Query(value = "SELECT * FROM pz_job j ORDER BY created_at DESC ", nativeQuery=true)
    Optional<List<PzJob>> listJobs();

    @Query(value =
            "SELECT " +
                "j.uuid, j.code, j.name, j.description, j.class_name, j.type, j.client_uuid, j.cron_expression, " +
                "j.cron_description, j.start_time, j.end_time, CAST(j.endpoint_api AS VARCHAR), st.repeat_count, " +
                "st.repeat_interval, st.times_triggered, t.prev_fire_time as last_execution, " +
                "t.next_fire_time as next_execution, j.created_at, j.created_by, j.updated_at, j.updated_by, j.status " +
            "FROM pz_job j " +
            "LEFT JOIN qrtz_job_details jd ON jd.job_name = j.uuid " +
            "LEFT JOIN qrtz_triggers t ON t.job_name = jd.job_name " +
            "LEFT JOIN qrtz_simple_triggers st ON st.trigger_name = t.trigger_name " +
            "WHERE j.uuid = :jobUuid " +
            "LIMIT 1 ", nativeQuery = true)
    Map<String, Object> getJobDetail(String jobUuid);

    @Query(value = "SELECT trigger_state FROM qrtz_triggers WHERE job_name = :jobUuid", nativeQuery=true)
    Optional<String> getJobDetailStatus(String jobUuid);
}

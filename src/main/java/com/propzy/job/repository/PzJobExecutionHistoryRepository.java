package com.propzy.job.repository;

import com.propzy.job.entity.PzJobExecutionHistory;
import org.hibernate.jpa.TypedParameterValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@Repository
public interface PzJobExecutionHistoryRepository extends JpaRepository<PzJobExecutionHistory, Long> {

    Optional<PzJobExecutionHistory> findFirstByUuid(String uuid);

    @Query(value=
            " SELECT CAST(h.uuid as varchar), CAST(h.job_uuid as varchar), CAST(j.client_uuid as varchar), " +
                " h.execution_date, h.execution_status, h.error_message, h.created_at, h.updated_at " +
            " FROM pz_job_execution_history h " +
            " JOIN pz_job j on h.job_uuid = j.uuid " +
            " WHERE (cast(:clientUuid as varchar) is null or j.client_uuid = cast(:clientUuid as varchar)) " +
                " AND (cast(:jobUuid as varchar) is null or h.job_uuid = cast(:jobUuid as varchar)) " +
                " AND (cast(:dateFrom as timestamptz) is null or h.execution_date >= cast(:dateFrom as timestamptz))" +
                " AND (cast(:dateTo as timestamptz) is null or h.execution_date <= cast(:dateTo as timestamptz))" +
                " AND(:executionStatus is null or h.execution_status = cast(:executionStatus as varchar))" +
                " AND (:errorMessage is null or h.error_message = cast(:errorMessage as text))", nativeQuery = true)
    Page<Map<String, Object>> filter(String clientUuid, String jobUuid,
                                     TypedParameterValue dateFrom, TypedParameterValue dateTo,
                                     String executionStatus, String errorMessage, Pageable pageable);

}

/*
package com.propzy.job.repository;

import com.propzy.job.constant.enums.ExecutionStatus;
import com.propzy.job.entity.PzJobExecutionHistory;
import org.assertj.core.api.Assertions;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.TimestampType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.Tuple;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PzJobExecutionHistoryRepositoryTest {
    @Autowired
    private PzJobExecutionHistoryRepository executionHistoryRepo;
    @Test
    void findFirstByUuid() {
        String uuid = "9641a4d1-352e-4992-bf29-4cb4d68abd7d";
        Optional<PzJobExecutionHistory> executionHistory = executionHistoryRepo.findFirstByUuid(uuid);

        executionHistory.ifPresent(h ->
                Assertions.assertThat(ExecutionStatus.SUCCESS.equals(h.getExecutionStatus())));
    }

    @Test
    void filter() {
        String clientUuid = "f4a9ba73-7ea1-4c45-ae84-55298d205f37";
        String jobUuid = "1e684ad2-e3d3-433d-a5eb-6d57cb70ebea";
        String errorMessage = "test";
        TypedParameterValue dateFrom = new TypedParameterValue(new TimestampType(), new Date(1652697691245l));
        TypedParameterValue dateTo = new TypedParameterValue(new TimestampType(), new Date(1652697691245l));
        Optional<List<Tuple>> results = executionHistoryRepo.filter(clientUuid,jobUuid, dateFrom, dateTo,
                "SUCCESS", errorMessage);

        results.ifPresent(list -> Assertions.assertThat(list.size() > 0));
    }
}*/

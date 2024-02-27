package com.propzy.job.unittest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.core.common.job.constant.enums.JobType;
import com.propzy.core.common.job.dto.EndpointApi;
import com.propzy.core.common.job.dto.request.CommonChangeJobRequest;
import com.propzy.job.constant.enums.JobStatus;
import com.propzy.job.dto.response.JobDetailResponse;
import com.propzy.job.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobTest {
    JobService jobService;

    @Test
    void getDetail() throws JsonProcessingException {
        CommonChangeJobRequest request = new CommonChangeJobRequest();
        request.setJobUuid(UUID.fromString("d87725c0-ad05-4d7a-b09c-41c62e13e0c1"));
        request.setClientUuid(UUID.fromString("534551bc-1397-43bc-85a2-87a5e70e61d8"));
        request.setSignature("1ea84546f888cd7e600952719c8149e22cb1858f042716b4445e0c5cf93f73d1");

        JobDetailResponse expectResponse = new JobDetailResponse();
        expectResponse.setUuid("d87725c0-ad05-4d7a-b09c-41c62e13e0c1");
        expectResponse.setCode("JOB_1_1653391199719");
        expectResponse.setName("Job 1");
        expectResponse.setDescription("Test job 1");
        expectResponse.setClassName("com.propzy.job.job.CommonCronJob");
        expectResponse.setType(JobType.CRON);
        expectResponse.setClientUuid("534551bc-1397-43bc-85a2-87a5e70e61d8");
        expectResponse.setCronExpression("0/30 * * 1/1 * ? *");
        expectResponse.setStartTime(new Date(1653397200000L));
        expectResponse.setLastExecution(new Date(-1L));
        expectResponse.setNextExecution(new Date(1653397200000L));

        EndpointApi endpoint = new EndpointApi();
        Map<String, Object> dataRequest = new HashMap<>();
        dataRequest.put("field1", "value1");
        dataRequest.put("field2", 2);
        endpoint.setDataRequest(dataRequest);
        endpoint.setUrl("http://localhost:8081/test1/api/test/test1");
        expectResponse.setEndpoint(endpoint);

        expectResponse.setCreatedAt(new Date(1653391199722L));
        expectResponse.setUpdatedAt(new Date(1653391199722L));
        expectResponse.setStatus(JobStatus.RUNNING);
        expectResponse.setStatusDetail("WAITING");

        JobDetailResponse actualResponse = jobService.getDetail(request);
        assertEquals(expectResponse, actualResponse);
    }
}

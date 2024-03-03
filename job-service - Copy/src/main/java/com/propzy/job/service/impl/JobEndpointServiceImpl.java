package com.propzy.job.service.impl;

import com.propzy.job.dto.JobEndpointClientResponse;
import com.propzy.job.service.JobEndpointService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobEndpointServiceImpl implements JobEndpointService {
    @SneakyThrows
    @Override
    public JobEndpointClientResponse callToJobEndpoint(String jobUuid, String spanId) {
        log.info("callToJobEndpoint, spanId = '{}'", spanId);
        return new JobEndpointClientResponse();
    }
}

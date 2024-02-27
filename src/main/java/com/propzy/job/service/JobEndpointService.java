package com.propzy.job.service;

import com.propzy.job.dto.JobEndpointClientResponse;

public interface JobEndpointService {
    JobEndpointClientResponse<Object> callToJobEndpoint(String jobUuid, String spanId);
}

package com.propzy.job.feignclient;

import com.propzy.job.dto.JobEndpointClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.util.Map;

@FeignClient(name = "job-endpoint",  url = "${config.endpoint-service.default-api}")
public interface JobEndpointFeignClient {
    @PostMapping("")
    JobEndpointClientResponse callToJobEndpoint(URI baseUrl, Map<String, Object> request);
}

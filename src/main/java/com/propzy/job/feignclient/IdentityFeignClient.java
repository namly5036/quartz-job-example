package com.propzy.job.feignclient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.propzy.job.dto.IdentityAccessTokenResponse;

@FeignClient(
        name = "identity",
        url = "${config.identity.host}")
public interface IdentityFeignClient {
    @PostMapping(value = "/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    IdentityAccessTokenResponse getToken(Map<String, ?> request);
}

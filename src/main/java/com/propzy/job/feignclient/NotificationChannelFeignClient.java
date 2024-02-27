package com.propzy.job.feignclient;

import com.propzy.job.constant.Constants;
import com.propzy.job.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "notification-channel",
        url = "${config.notification-channel.host}")
public interface NotificationChannelFeignClient {
    @GetMapping(value = "/client-service/secret-key/{serviceId}")
    ApiResponse<String> getClientSecretkey(@PathVariable UUID serviceId, @RequestHeader(value = Constants.HEADER_AUTHENTICATION) String auth);
}

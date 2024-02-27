package com.propzy.job.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.core.common.exception.UnresolvedServiceException;
import com.propzy.core.common.job.dto.request.BaseRequest;
import com.propzy.job.dto.ApiResponse;
import com.propzy.job.exception.CustomInvalidParameterException;
import com.propzy.job.exception.IdErrorCode;
import com.propzy.job.feignclient.NotificationChannelFeignClient;
import com.propzy.job.service.IdentityService;
import com.propzy.job.service.NotificationChannelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationChannelServiceImpl implements NotificationChannelService {
    IdentityService identityService;
    NotificationChannelFeignClient notiChannelFeignClient;

    @Override
    public void verifyClient(BaseRequest request) throws JsonProcessingException {
        log.info("===== Verify Client Id ");
        String accessToken = identityService.getAccessToken(); //TODO handle Feign exception/feign.FeignException$Unauthorized
        if (StringUtils.isBlank(accessToken)) {
            throw new UnresolvedServiceException(IdErrorCode.CANNOT_GET_CLIENT_SECRET);
        }
        ApiResponse<String> response = notiChannelFeignClient.getClientSecretkey(request.getClientUuid(), "Bearer " + accessToken);
        if (response.getCode() != HttpStatus.OK.value() //TODO validate more
            || StringUtils.isBlank(response.getData())) {
            throw new UnresolvedServiceException(IdErrorCode.CANNOT_GET_CLIENT_SECRET);
        }
        String requestSignature = request.getSignature();

        request.setSignature(null);
        String verifySignature = request.genSignature(response.getData());
        if (!requestSignature.equals(verifySignature)) {
            throw new CustomInvalidParameterException(IdErrorCode.INVALID_REQUEST, "Client Id or Client Secret");
        }
    }
}

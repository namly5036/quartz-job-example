package com.propzy.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.propzy.core.common.job.dto.request.BaseRequest;

public interface NotificationChannelService {
    void verifyClient(BaseRequest request) throws JsonProcessingException;
}

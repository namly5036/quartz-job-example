package com.propzy.job.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.propzy.job.constant.Constants;
import com.propzy.job.dto.IdentityAccessTokenResponse;
import com.propzy.job.feignclient.IdentityFeignClient;
import com.propzy.job.service.IdentityService;

@Service
public class IdentityServiceImpl implements IdentityService {
    @Value("${config.identity.client.id}")
    private String clientId;

    @Value("${config.identity.client.secret}")
    private String clientSecret;


    @Autowired
    private IdentityFeignClient identityClient;

    @Override
    public String getAccessToken() {
        Map<String, String> request = new HashMap<>();
        request.put(Constants.CLIENT_ID, clientId);
        request.put(Constants.CLIENT_SECRET, clientSecret);
        request.put(Constants.GRANT_TYPE, Constants.CLIENT_CREDENTIALS_GRANT_TYPE);
        IdentityAccessTokenResponse response = identityClient.getToken(request);
        return response.getAccessToken();
    }

}

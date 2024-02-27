package com.propzy.job.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propzy.core.common.hashing.HmacUtils;
import com.propzy.job.constant.Constants;
import com.propzy.job.constant.enums.FieldDefine;
import com.propzy.job.dto.JobEndpointClientResponse;
import com.propzy.job.exception.CustomResourceNotFoundException;
import com.propzy.job.exception.IdErrorCode;
import com.propzy.job.feignclient.JobEndpointFeignClient;
import com.propzy.job.repository.PzJobRepository;
import com.propzy.job.service.JobEndpointService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobEndpointServiceImpl implements JobEndpointService {
    PzJobRepository pzJobRepo;
    JobEndpointFeignClient jobEndpointFeignClient;
    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public JobEndpointClientResponse callToJobEndpoint(String jobUuid, String spanId) {
        final var job = pzJobRepo.findByUuid(jobUuid)
                .orElseThrow(() -> new CustomResourceNotFoundException(IdErrorCode.DATA_NOTFOUND, "Job"));
        final var url = job.getEndpointApi().getUrl();
        Map<String, Object> requestData = job.getEndpointApi().getDataRequest();
        requestData.put(FieldDefine.SPAN_ID.getFieldName(), spanId);
        log.info("===== Endpoint URL = {}", url);
        String requestJSONStr = objectMapper.writeValueAsString(requestData);
        log.info("===== Endpoint Request Data = {}", requestJSONStr);
        final var baseUri = URI.create(url);

        final var signature = HmacUtils.getInstance().hmacHexStringEncode(Constants.HASH_ALGORITHM,
                job.getJobGroup(), requestJSONStr);
        requestData.put(FieldDefine.SIGNATURE.getFieldName(), signature);
        return jobEndpointFeignClient.callToJobEndpoint(baseUri, requestData);//where baseUri is used???
    }
}

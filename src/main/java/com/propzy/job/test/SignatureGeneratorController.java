package com.propzy.job.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propzy.core.common.job.dto.request.CommonChangeJobRequest;
import com.propzy.core.common.job.dto.request.CommonSchedulerJobRequest;
import com.propzy.job.dto.ApiResponse;
import com.propzy.job.dto.request.ExecutionHistoryRequest;
import com.propzy.job.dto.request.FilterJobRequest;
import com.propzy.job.feignclient.NotificationChannelFeignClient;
import com.propzy.job.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/signature/job")
public class SignatureGeneratorController {
    IdentityService identityService;
    NotificationChannelFeignClient notiChannelFeignClient;
    ObjectMapper objectMapper;
    
    private String getClientSecretKey(UUID clientUuid) {
        String accessToken = identityService.getAccessToken();
        ApiResponse<String> response = notiChannelFeignClient.getClientSecretkey(clientUuid, "Bearer " + accessToken);
        return response.getData();
    }

    @PostMapping("/detail")
    public String detail(@RequestBody CommonChangeJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @PostMapping("/create")
    public String create(@RequestBody CommonSchedulerJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @PutMapping("/update")
    public String update(@RequestBody CommonSchedulerJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @DeleteMapping("/delete")
    public String delete(@RequestBody CommonChangeJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @PutMapping("/pause")
    public String pause(@RequestBody CommonChangeJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @PutMapping("/resume")
    public String resume(@RequestBody CommonChangeJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @PostMapping("/filter")
    public String filterJobs(@RequestBody FilterJobRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }

    @PostMapping("/execution-history/filter")
    public String filterExecutionHistory(@RequestBody ExecutionHistoryRequest request) throws JsonProcessingException {
        return request.genSignature(this.getClientSecretKey(request.getClientUuid()));
    }
}

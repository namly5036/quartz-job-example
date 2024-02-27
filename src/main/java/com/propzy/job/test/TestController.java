package com.propzy.job.test;

import com.propzy.job.constant.enums.ExecutionStatus;
import com.propzy.job.dto.JobEndpointClientResponse;
import com.propzy.job.dto.request.ReceiveCallbackRequest;
import com.propzy.job.exception.CustomResourceDuplicatedException;
import com.propzy.job.exception.IdErrorCode;
import com.propzy.job.exception.ServiceException;
import com.propzy.job.repository.PzJobRepository;
import com.propzy.job.service.JobEndpointService;
import com.propzy.job.service.NotificationChannelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/test")
public class TestController {
    JobEndpointService jobEndpointService;
    PzJobRepository jobRepository;
    NotificationChannelService notiChannelService;
    @NonFinal
    static ThreadPoolExecutor taskExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    @NonFinal
    static RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/test1")
    public Object test1(@RequestBody Map<String, Object> request) {
        System.out.println("===== " + request);
        taskExecutor.execute(() -> {
            try {
                Thread.sleep(5000);
                ReceiveCallbackRequest callbackRequest = new ReceiveCallbackRequest();
                callbackRequest.setSpanId(UUID.fromString((String) request.get("span_id")));
                callbackRequest.setStatus(ExecutionStatus.SUCCESS);
                restTemplate.postForObject("https://dev-core-team-v1.k8s.propzy.asia/job-management/job/receive-call-back", callbackRequest, Void.class);
                log.info("===== Call Back");
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        });
        JobEndpointClientResponse response = new JobEndpointClientResponse();
        response.setStatus(ExecutionStatus.PROCESSING);
        log.info("===== Response PROCESSING");
        return response;
    }

    @PostMapping("/test2")
    public Object test2(@RequestBody Map<String, Object> request) {
        throw new CustomResourceDuplicatedException(IdErrorCode.RESOURCE_DUPLICATE, "Error cmnr");
    }
}


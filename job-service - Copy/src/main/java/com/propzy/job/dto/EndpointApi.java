package com.propzy.job.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EndpointApi {
    private Map<String, Object> pathParams;
    private Map<String, Object> dataRequest;
    private String url;
}

package com.propzy.job.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CommonChangeJobRequest {
    private UUID jobId;
}

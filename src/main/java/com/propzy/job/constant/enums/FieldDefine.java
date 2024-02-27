package com.propzy.job.constant.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum FieldDefine {
    URL("url"),
    PATH_PARAMS("path_params"),
    DATA_REQUEST("data_request"),
    JOB_UUID("job_uuid"),
    SIGNATURE("signature"),
    ENDPOINT_API("endpoint_api"),
    SPAN_ID("span_id");

    String fieldName;
}

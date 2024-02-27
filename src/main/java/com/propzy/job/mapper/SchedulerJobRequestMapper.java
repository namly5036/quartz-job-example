package com.propzy.job.mapper;

import com.propzy.core.common.job.dto.request.CommonSchedulerJobRequest;
import com.propzy.job.dto.request.SchedulerJobRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SchedulerJobRequestMapper {
    SchedulerJobRequestMapper INSTANCE = Mappers.getMapper(SchedulerJobRequestMapper.class);

    SchedulerJobRequest CommonToSchedulerJobRequest(CommonSchedulerJobRequest commonDto);
}

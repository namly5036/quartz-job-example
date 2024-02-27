package com.propzy.job;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.propzy.job.entity.PzJob;
import com.propzy.job.repository.PzJobRepository;
import com.propzy.job.service.impl.JobServiceImpl;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private PzJobRepository jobRepository;
    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    public void filterJobsTest() {
        // 1. create mock data
        List<PzJob> mockJobs = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            mockJobs.add(new PzJob());
        }

//        // 2. define behavior of Repository
//        when(jobRepository.filterJobs(null,null,null)).thenReturn(Optional.ofNullable(mockJobs));
//
//        // 3. call service method
//        List<JobDto> actualJobs = jobService.filter(new FilterJobRequest());
//
//        // 4. assert the result
//        assertThat(actualJobs.size()).isEqualTo(mockJobs.size());
//
//        // 4.1 ensure repository is called
//        verify(jobRepository).filterJobs(null,null,null);

    }
}

package com.propzy.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
public class ServiceConfig {

//	@Value("${config.thread.thread-name-prefix}")
//	private String threadNamePrefix;
//
//	@Value("${config.thread.core-pool-size}")
//	private int corePoolSize;
//
//	@Value("${config.thread.max-pool-size}")
//	private int maxPoolSize;
//
//	@Value("${config.thread.queue-capacity}")
//	private int queueCapacity;
//
//	@Bean(name = "taskExecutor")
//	public Executor taskExecutor() {
//		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(corePoolSize);
//		executor.setMaxPoolSize(maxPoolSize);
//		executor.setQueueCapacity(queueCapacity);
//		executor.setThreadNamePrefix(threadNamePrefix);
//		executor.initialize();
//		return executor;
//	}

	@Bean(name = "objectMapper")
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}

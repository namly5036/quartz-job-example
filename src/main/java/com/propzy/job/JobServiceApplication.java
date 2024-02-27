package com.propzy.job;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@EnableFeignClients
@Slf4j
@SpringBootApplication
public class JobServiceApplication {
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
  public static void main(String[] args) {
    SpringApplication.run(JobServiceApplication.class, args);
  }
}

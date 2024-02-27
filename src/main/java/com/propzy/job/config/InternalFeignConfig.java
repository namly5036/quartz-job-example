package com.propzy.job.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

public class InternalFeignConfig {

  @Bean
  public Encoder internalEncoder(ObjectFactory<HttpMessageConverters> converters) {
    return new SpringFormEncoder(new SpringEncoder(converters));
  }
}

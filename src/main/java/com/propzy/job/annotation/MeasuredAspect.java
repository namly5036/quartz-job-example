package com.propzy.job.annotation;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Configuration
public class MeasuredAspect {

    @Around("@annotation(com.propzy.job.annotation.Measured)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Measured measured = method.getAnnotation(Measured.class);
        String message = measured.message();
        if (StringUtils.isBlank(message))
            log.info(
                    "Method {} Execution: {} ms", joinPoint.getSignature().toShortString(), executionTime);
        else log.info("{}: {} ms", message, executionTime);
        return proceed;
    }
}

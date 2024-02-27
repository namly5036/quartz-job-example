package com.propzy.job.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Value("${spring.application.name}")
    private String serviceName;

    @Pointcut("execution(* com.propzy.job.controller.*.*(..))")
    public void log() {
        throw new UnsupportedOperationException();
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
                HttpServletRequest request = attributes.getRequest();
                String classMethod =
                        joinPoint.getSignature().getDeclaringTypeName()
                                + "."
                                + joinPoint.getSignature().getName();
                Object[] args = joinPoint.getArgs();
                ObjectMapper mapper = new ObjectMapper();
                log.info("Content-Type: " + request.getContentType());
                log.info("Method: " + request.getMethod());
                log.info("QueryString: " + request.getQueryString());
                log.info("RemoteAddr: " + request.getRemoteAddr());
                log.info("RequestURI: " + request.getRequestURI());
                log.info("RequestURL: " + request.getRequestURL());
                log.info("ClassMethod: " + classMethod);
                if (args.length == 1) {
                    // FIXME: failed for multipart
                    log.info("Request Body: {}", mapper.writeValueAsString(args[0]));

                } else if (args.length > 1) {
                    log.info("Request Body: {}", mapper.writeValueAsString(args));
                }
            } catch (Exception ex) {
                log.error("{}", ex.getMessage(), ex);
            }
        }
    }

    @Around("log()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            String className = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.info("Method {}.{}(). Execution time: {} ms", className, methodName, elapsedTime);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in "
                    + joinPoint.getSignature().getName() + "()");
            throw e;
        }
    }
}

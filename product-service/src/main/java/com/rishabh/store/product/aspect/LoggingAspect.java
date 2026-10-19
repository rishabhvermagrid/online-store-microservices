package com.rishabh.store.product.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Before("execution(* com.rishabh.store.product.controller..*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint){
          log.info("Executing method: {}",joinPoint.getSignature().getName());
    }

    @Around("execution(* com.rishabh.store.product.controller..*.*(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint)
            throws Throwable {

        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed(); // calls actual service method
        } finally {
            long timeTaken = System.currentTimeMillis() - start;

            log.info("{} took {} ms",joinPoint.getSignature().getName(),timeTaken);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.rishabh.store.product.controller..*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex){
        log.error("Exception in method: {} with args: {}. Error: {}",joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()),ex.getMessage());
    }
}


package com.project.fitness.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.project.fitness..controller..*)")
    public void controllerPointcut() {
        // Pointcut for all REST controllers
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        if (log.isInfoEnabled()) {
            log.info("Enter: {}.{}() with argument[s] = {}", className, methodName, Arrays.toString(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            
            if (log.isInfoEnabled()) {
                log.info("Exit: {}.{}() with result = {}, Execution time: {} ms", className, methodName, result, elapsedTime);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()), className, methodName);
            throw e;
        } catch (Exception ex) {
            log.error("Exception in {}.{}() with cause = {}", className, methodName, ex.getCause() != null ? ex.getCause() : "NULL");
            throw ex;
        }
    }
}

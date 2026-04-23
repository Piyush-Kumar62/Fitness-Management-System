package com.project.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@Slf4j
public class FitnessManagementSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(FitnessManagementSystemApplication.class, args);
    log.info("=========================================================");
    log.info("Application started successfully at http://localhost:8080");
    log.info("=========================================================");

  }

}

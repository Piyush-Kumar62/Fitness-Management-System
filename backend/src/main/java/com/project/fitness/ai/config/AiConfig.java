package com.project.fitness.ai.config;

import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {

  @Bean
  public WebClient.Builder aiWebClientBuilder(AiProperties properties) {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getHttp().getConnectTimeoutMs())
        .responseTimeout(properties.getHttp().getResponseTimeout());
    return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
  }
}

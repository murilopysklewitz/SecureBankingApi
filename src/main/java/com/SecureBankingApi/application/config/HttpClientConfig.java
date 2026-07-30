package com.SecureBankingApi.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

public class HttpClientConfig {
    @Bean
    RestClient restClient() {
        return RestClient.create();
    }
}

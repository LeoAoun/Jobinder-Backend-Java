package br.com.jobinder.authservice.infra.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${service.api-key.header:X-API-KEY}")
    private String apiKeyHeader;

    @Value("${service.api-key.value}")
    private String apiKeyValue;

    @Override
    public void apply(RequestTemplate template) {
        // Add the API key header to all outgoing requests
        template.header(apiKeyHeader, apiKeyValue);
    }
}
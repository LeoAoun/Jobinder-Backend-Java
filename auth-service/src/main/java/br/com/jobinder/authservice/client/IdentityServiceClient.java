package br.com.jobinder.authservice.client;

import br.com.jobinder.authservice.dto.InternalUserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "${identity-service.url}")
public interface IdentityServiceClient {

    @GetMapping("/internal/users/{phone}")
    InternalUserAuthDTO getUserAuthDetails(@PathVariable("phone") String phone);
}
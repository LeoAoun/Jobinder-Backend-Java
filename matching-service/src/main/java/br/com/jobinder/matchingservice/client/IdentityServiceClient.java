package br.com.jobinder.matchingservice.client;

import br.com.jobinder.matchingservice.dto.ProfileUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identity-service", url = "${identity-service.url}")
public interface IdentityServiceClient {
    @GetMapping("/api/v1/service-profiles/{profileId}/user")
    ProfileUserDTO getUserIdByProfileId(@PathVariable("profileId") UUID profileId);
}
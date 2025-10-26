package br.com.jobinder.identityservice.domain.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Optional<Location> findByCityIgnoreCaseAndStateIgnoreCase(String city, String state);
}

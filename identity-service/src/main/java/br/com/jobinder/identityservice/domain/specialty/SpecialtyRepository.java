package br.com.jobinder.identityservice.domain.specialty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    Optional<Specialty> findByNameIgnoreCase(String name);
}

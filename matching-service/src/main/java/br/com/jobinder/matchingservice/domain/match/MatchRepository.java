package br.com.jobinder.matchingservice.domain.match;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    // Check if a match exists between a client and a professional
    boolean existsByClientUserIdAndProfessionalUserId(UUID clientUserId, UUID professionalUserId);

    List<Match> findAllByClientUserId(UUID clientUserId);

    List<Match> findAllByProfessionalUserId(UUID professionalUserId);
}
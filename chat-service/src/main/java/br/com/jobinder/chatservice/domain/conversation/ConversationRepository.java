package br.com.jobinder.chatservice.domain.conversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findAllByClientUserIdOrProfessionalUserId(UUID clientUserId, UUID professionalUserId);

    boolean existsByMatchId(UUID matchId);

    Optional<Conversation> findByMatchId(UUID matchId);
}
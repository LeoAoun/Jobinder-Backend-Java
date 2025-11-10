package br.com.jobinder.matchingservice.service;

import br.com.jobinder.matchingservice.client.IdentityServiceClient;
import br.com.jobinder.matchingservice.domain.match.Match;
import br.com.jobinder.matchingservice.domain.match.MatchRepository;
import br.com.jobinder.matchingservice.dto.MatchCreateDTO;
import br.com.jobinder.matchingservice.dto.MatchCreatedEvent;
import br.com.jobinder.matchingservice.dto.MatchResponseDTO;
import br.com.jobinder.matchingservice.infra.exception.MatchAlreadyExistsException;
import br.com.jobinder.matchingservice.infra.exception.MatchNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private IdentityServiceClient identityServiceClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queues.match-created}")
    private String matchCreatedQueue;

    @Transactional
    public MatchResponseDTO createMatch(UUID clientUserId, MatchCreateDTO createDto) {
        var professionalUserId = createDto.professionalUserId();

        // Check if a match already exists between the client and professional
        if (matchRepository.existsByClientUserIdAndProfessionalUserId(clientUserId, professionalUserId)) {
            throw new MatchAlreadyExistsException("A connection between these users already exists.");
        }

        // Create and save the match using between client and professional user IDs
        var match = new Match(null, clientUserId, professionalUserId, null);
        var savedMatch = matchRepository.save(match);

        // Publish event to RabbitMQ to enable chat
        var event = new MatchCreatedEvent(savedMatch.getId(), clientUserId, savedMatch.getProfessionalUserId());
        rabbitTemplate.convertAndSend(matchCreatedQueue, event);

        return new MatchResponseDTO(savedMatch.getId(), "Match created successfully and chat enabled.");
    }

    public MatchResponseDTO findMatchById(UUID matchId) {
        return matchRepository.findById(matchId)
                .map(match -> new MatchResponseDTO(match.getId(), "Match details found."))
                .orElseThrow(() -> new MatchNotFoundException("Match not found with ID: " + matchId));
    }

    public List<MatchResponseDTO> findMatchesByClientId(UUID clientUserId) {
        return matchRepository.findAllByClientUserId(clientUserId)
                .stream()
                .map(match -> new MatchResponseDTO(match.getId(), "Match with professional " + match.getProfessionalUserId()))
                .collect(Collectors.toList());
    }

    public List<MatchResponseDTO> findMatchesByProfessionalId(UUID professionalUserId) {
        return matchRepository.findAllByProfessionalUserId(professionalUserId)
                .stream()
                .map(match -> new MatchResponseDTO(match.getId(), "Match with client " + match.getClientUserId()))
                .collect(Collectors.toList());
    }

    public List<Match> getAllMatchesDTO() {
        return matchRepository.findAll();
    }

}
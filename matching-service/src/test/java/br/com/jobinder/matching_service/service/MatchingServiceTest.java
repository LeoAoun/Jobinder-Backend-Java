package br.com.jobinder.matching_service.service;

import br.com.jobinder.matchingservice.client.IdentityServiceClient;
import br.com.jobinder.matchingservice.domain.match.Match;
import br.com.jobinder.matchingservice.domain.match.MatchRepository;
import br.com.jobinder.matchingservice.dto.MatchCreateDTO;
import br.com.jobinder.matchingservice.dto.MatchCreatedEvent;
import br.com.jobinder.matchingservice.dto.MatchResponseDTO;
import br.com.jobinder.matchingservice.infra.exception.MatchAlreadyExistsException;
import br.com.jobinder.matchingservice.infra.exception.MatchNotFoundException;
import br.com.jobinder.matchingservice.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private IdentityServiceClient identityServiceClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(matchingService, "matchCreatedQueue", "q.match-created");
    }

    @Test
    @DisplayName("Should create match and publish event when match does not exist")
    void createMatch_WhenMatchDoesNotExist_ShouldSaveAndPublishEvent() {
        // Given
        UUID clientUserId = UUID.randomUUID();
        UUID professionalUserId = UUID.randomUUID();
        var createDto = new MatchCreateDTO(professionalUserId);

        // Mocking
        when(matchRepository.existsByClientUserIdAndProfessionalUserId(clientUserId, professionalUserId))
                .thenReturn(false);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match match = invocation.getArgument(0);
            match.setId(UUID.randomUUID());
            return match;
        });

        // When
        MatchResponseDTO response = matchingService.createMatch(clientUserId, createDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.matchId());
        assertEquals("Match created successfully and chat enabled.", response.message());

        // Verify
        verify(matchRepository).save(any(Match.class));
        verify(rabbitTemplate).convertAndSend(
                eq("q.match-created"),
                any(MatchCreatedEvent.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when match already exists")
    void createMatch_WhenMatchAlreadyExists_ShouldThrowException() {
        // Given
        UUID clientUserId = UUID.randomUUID();
        UUID professionalUserId = UUID.randomUUID();
        var createDto = new MatchCreateDTO(professionalUserId);

        // Mocking
        when(matchRepository.existsByClientUserIdAndProfessionalUserId(clientUserId, professionalUserId))
                .thenReturn(true);

        // When & Then
        assertThrows(MatchAlreadyExistsException.class, () -> {
            matchingService.createMatch(clientUserId, createDto);
        });

        // Verify
        verify(matchRepository, never()).save(any(Match.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(MatchCreatedEvent.class));
    }

    @Test
    @DisplayName("Should return match DTO when found by ID")
    void findMatchById_WhenFound_ShouldReturnDTO() {
        // Given
        UUID matchId = UUID.randomUUID();
        Match match = new Match(matchId, UUID.randomUUID(), UUID.randomUUID(), null);

        // Mocking
        when(matchRepository.findById(matchId))
                .thenReturn(Optional.of(match));

        // When
        MatchResponseDTO result = matchingService.findMatchById(matchId);

        // Then
        assertNotNull(result);
        assertEquals(matchId, result.matchId());
    }

    @Test
    @DisplayName("Should throw exception when match not found by ID")
    void findMatchById_WhenNotFound_ShouldThrowException() {
        // Given
        UUID matchId = UUID.randomUUID();
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MatchNotFoundException.class, () -> {
            matchingService.findMatchById(matchId);
        });
    }

    @Test
    @DisplayName("Should return list of matches for client ID")
    void findMatchesByClientId_ShouldReturnList() {
        // Given
        UUID clientId = UUID.randomUUID();
        Match match1 = new Match(
                UUID.randomUUID(),
                clientId,
                UUID.randomUUID(),
                null
        );
        Match match2 = new Match(
                UUID.randomUUID(),
                clientId,
                UUID.randomUUID(),
                null
        );

        // Mocking
        when(matchRepository.findAllByClientUserId(clientId))
                .thenReturn(List.of(match1, match2));

        // When
        List<MatchResponseDTO> result = matchingService.findMatchesByClientId(clientId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return list of matches for professional ID")
    void findMatchesByProfessionalId_ShouldReturnList() {
        // Given
        UUID professionalId = UUID.randomUUID();
        Match match1 = new Match(
                UUID.randomUUID(),
                UUID.randomUUID(),
                professionalId,
                null
        );

        // Mocking
        when(matchRepository.findAllByProfessionalUserId(professionalId))
                .thenReturn(List.of(match1));

        // When
        List<MatchResponseDTO> result = matchingService.findMatchesByProfessionalId(professionalId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return all matches (Admin)")
    void getAllMatchesDTO_ShouldReturnList() {
        // Given
        Match match1 = new Match(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null
        );

        // Mocking
        when(matchRepository.findAll())
                .thenReturn(List.of(match1));

        // When
        List<Match> result = matchingService.getAllMatchesDTO();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
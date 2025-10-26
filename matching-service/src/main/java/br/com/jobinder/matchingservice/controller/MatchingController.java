package br.com.jobinder.matchingservice.controller;

import br.com.jobinder.matchingservice.domain.match.Match;
import br.com.jobinder.matchingservice.dto.MatchCreateDTO;
import br.com.jobinder.matchingservice.dto.MatchResponseDTO;
import br.com.jobinder.matchingservice.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @PostMapping
    public ResponseEntity<MatchResponseDTO> createMatch(@RequestBody MatchCreateDTO matchCreateDTO, Authentication authentication) {
        UUID clientUserId = UUID.fromString(authentication.getName());
        var response = matchingService.createMatch(clientUserId, matchCreateDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseDTO> getMatchById(@PathVariable UUID matchId) {
        var response = matchingService.findMatchById(matchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByClientId(@PathVariable UUID clientId) {
        var response = matchingService.findMatchesByClientId(clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByProfessionalId(@PathVariable UUID professionalId) {
        var response = matchingService.findMatchesByProfessionalId(professionalId);
        return ResponseEntity.ok(response);
    }

    /*
      Administrative Endpoints
      These endpoints are intended for administrative use only.
    */

    @GetMapping("/admin/all")
    public ResponseEntity<List<Match>> getAllMatchesDTO() {
        var response = matchingService.getAllMatchesDTO();
        return ResponseEntity.ok(response);
    }

}

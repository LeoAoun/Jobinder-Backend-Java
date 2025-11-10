package br.com.jobinder.matchingservice.controller;

import br.com.jobinder.matchingservice.domain.match.Match;
import br.com.jobinder.matchingservice.dto.MatchCreateDTO;
import br.com.jobinder.matchingservice.dto.MatchResponseDTO;
import br.com.jobinder.matchingservice.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @Operation(summary = "Create a new match",
            description = "Creates a match (like) from the authenticated user (assumed to be the 'client') to a professional. " +
                    "This action enables the chat between the two users.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request (invalid input data)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized (invalid or missing token)",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict (a match between these users already exists)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<MatchResponseDTO> createMatch(@RequestBody MatchCreateDTO matchCreateDTO, Authentication authentication) {
        UUID clientUserId = UUID.fromString(authentication.getName());
        var response = matchingService.createMatch(clientUserId, matchCreateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a specific match by ID",
            description = "Retrieves a specific match by its unique ID. " +
                    "Users can only retrieve matches they are part of (client or professional). Admins can retrieve any.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (user is not part of this match)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Match not found",
                    content = @Content)
    })
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseDTO> getMatchById(@PathVariable UUID matchId) {
        var response = matchingService.findMatchById(matchId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get matches by Client ID",
            description = "Retrieves all matches for a specific client. " +
                    "A user can only retrieve their own matches. Admins can retrieve any.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (user trying to access another user's matches)",
                    content = @Content)
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByClientId(@PathVariable UUID clientId) {
        var response = matchingService.findMatchesByClientId(clientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get matches by Professional ID",
            description = "Retrieves all matches for a specific professional. " +
                    "A user can only retrieve their own matches. Admins can retrieve any.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (user trying to access another user's matches)",
                    content = @Content)
    })
    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByProfessionalId(@PathVariable UUID professionalId) {
        var response = matchingService.findMatchesByProfessionalId(professionalId);
        return ResponseEntity.ok(response);
    }

    /*
      Administrative Endpoints
      These endpoints are intended for administrative use only.
    */

    @Operation(summary = "[Admin] List all matches",
            description = "Retrieves a list of all matches in the system. (Requires ADMIN role)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Match.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)", content = @Content)
    })
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Match>> getAllMatchesDTO() {
        var response = matchingService.getAllMatchesDTO();
        return ResponseEntity.ok(response);
    }

}

package br.com.jobinder.identityservice.controller;

import br.com.jobinder.identityservice.dto.location.LocationDTO;
import br.com.jobinder.identityservice.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Locations", description = "Endpoints for viewing service locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Operation(summary = "List all available locations",
            description = "Retrieves a public list of all service locations (city/state pairs) available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locations retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LocationDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        var locations = locationService.findAll();
        return ResponseEntity.ok(locations);
    }
}
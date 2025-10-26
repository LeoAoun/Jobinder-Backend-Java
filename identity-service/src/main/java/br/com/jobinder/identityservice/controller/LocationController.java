package br.com.jobinder.identityservice.controller;

import br.com.jobinder.identityservice.dto.location.LocationDTO;
import br.com.jobinder.identityservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        var locations = locationService.findAll();
        return ResponseEntity.ok(locations);
    }
}
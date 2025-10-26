package br.com.jobinder.identityservice.service;

import br.com.jobinder.identityservice.domain.location.Location;
import br.com.jobinder.identityservice.domain.location.LocationRepository;
import br.com.jobinder.identityservice.dto.location.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public Location findOrCreateLocation(String city, String state) {
        return locationRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state)
                .orElseGet(() -> {
                    var newLocation = new Location(null, city, state);
                    return locationRepository.save(newLocation);
                });
    }

    public List<LocationDTO> findAll() {
        return locationRepository.findAll()
                .stream()
                .map(l -> new LocationDTO(l.getId(), l.getCity(), l.getState()))
                .toList();
    }
}
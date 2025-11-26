package br.com.jobinder.identity_service.service;

import br.com.jobinder.identityservice.domain.location.Location;
import br.com.jobinder.identityservice.domain.location.LocationRepository;
import br.com.jobinder.identityservice.dto.location.LocationDTO;
import br.com.jobinder.identityservice.service.LocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    @DisplayName("Should return existing location when city and state already exist")
    void findOrCreateLocation_WhenLocationExists_ShouldReturnExisting() {
        // Given
        String city = "São Paulo";
        String state = "SP";
        Location existingLocation = new Location(UUID.randomUUID(), city, state);

        // Mocking
        when(locationRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state))
                .thenReturn(Optional.of(existingLocation));

        // When
        Location result = locationService.findOrCreateLocation(city, state);

        // Then
        assertNotNull(result);
        assertEquals(existingLocation.getId(), result.getId());
        assertEquals(city, result.getCity());
        assertEquals(state, result.getState());

        // Verify
        verify(locationRepository, never())
                .save(any(Location.class));
    }

    @Test
    @DisplayName("Should create and return new location when it does not exist")
    void findOrCreateLocation_WhenLocationDoesNotExist_ShouldCreateNew() {
        // Given
        String city = "Rio de Janeiro";
        String state = "RJ";

        // Mocking
        when(locationRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state))
                .thenReturn(Optional.empty());

        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location loc = invocation.getArgument(0);
            loc.setId(UUID.randomUUID()); // Simulate ID generation
            return loc;
        });

        // When
        Location result = locationService.findOrCreateLocation(city, state);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(city, result.getCity());
        assertEquals(state, result.getState());

        // Verify
        verify(locationRepository)
                .save(any(Location.class));
    }

    @Test
    @DisplayName("Should return all locations as DTOs")
    void findAll_ShouldReturnListDTO() {
        // Given
        Location loc1 = new Location(UUID.randomUUID(), "City1", "ST");
        Location loc2 = new Location(UUID.randomUUID(), "City2", "ST");

        // Mocking
        when(locationRepository.findAll())
                .thenReturn(List.of(loc1, loc2));

        // When
        List<LocationDTO> result = locationService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(loc1.getCity(), result.get(0).city());
        assertEquals(loc1.getState(), result.get(0).state());

        assertEquals(loc2.getCity(), result.get(1).city());

        // Verify
        verify(locationRepository)
                .findAll();
    }
}
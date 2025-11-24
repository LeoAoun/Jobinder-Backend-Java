package br.com.jobinder.identity_service.service;

import br.com.jobinder.identityservice.domain.location.Location;
import br.com.jobinder.identityservice.domain.serviceprofile.ServiceProfile;
import br.com.jobinder.identityservice.domain.serviceprofile.ServiceProfileRepository;
import br.com.jobinder.identityservice.domain.specialty.Specialty;
import br.com.jobinder.identityservice.domain.user.User;
import br.com.jobinder.identityservice.domain.user.UserRepository;
import br.com.jobinder.identityservice.dto.serviceprofile.ServiceProfileCreateDTO;
import br.com.jobinder.identityservice.dto.serviceprofile.ServiceProfileResponseDTO;
import br.com.jobinder.identityservice.infra.exception.serviceprofile.ServiceProfileAlreadyExistsException;
import br.com.jobinder.identityservice.infra.exception.serviceprofile.ServiceProfileNotFoundException;
import br.com.jobinder.identityservice.infra.exception.user.UserNotFoundException;
import br.com.jobinder.identityservice.service.LocationService;
import br.com.jobinder.identityservice.service.ServiceProfileService;
import br.com.jobinder.identityservice.service.SpecialtyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceProfileServiceTest {

    @Mock
    private ServiceProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private ServiceProfileService serviceProfileService;

    @Test
    @DisplayName("Should create service profile successfully when data is valid")
    void createServiceProfile_WithValidData_ShouldSaveAndReturnDTO() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        String specialtyName = "Software Engineer";
        String city = "São Paulo";
        String state = "SP";

        var serviceProfileCreateDTO = new ServiceProfileCreateDTO(
                specialtyName,
                city,
                state,
                "http://image.url",
                "Mon-Fri",
                "Experienced dev"
        );

        Location mockLocation = new Location(UUID.randomUUID(), city, state);
        Specialty mockSpecialty = new Specialty(UUID.randomUUID(), specialtyName);

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(profileRepository.existsByUserId(userId))
                .thenReturn(false);
        when(locationService.findOrCreateLocation(serviceProfileCreateDTO.city(), serviceProfileCreateDTO.state()))
                .thenReturn(mockLocation);
        when(specialtyService.findOrCreateSpecialty(serviceProfileCreateDTO.specialtyName()))
                .thenReturn(mockSpecialty);
        when(profileRepository.save(any(ServiceProfile.class)))
                .thenAnswer(invocation -> {
            ServiceProfile profile = invocation.getArgument(0);
            profile.setId(UUID.randomUUID());
            profile.setCreatedAt(LocalDateTime.now());
            return profile;
        });

        // When
        ServiceProfileResponseDTO response = serviceProfileService.createServiceProfile(serviceProfileCreateDTO, userId);

        // Then
        assertNotNull(response);
        assertNotNull(response.serviceProfileId());
        assertEquals(userId, response.userId());
        assertEquals(mockSpecialty.getName(), response.specialtyName());
        assertEquals(mockLocation.getCity(), response.locationCity());

        // Verify
        verify(profileRepository)
                .save(any(ServiceProfile.class));
    }

    @Test
    @DisplayName("Should throw exception when authenticated user is not found")
    void createServiceProfile_WhenUserNotFound_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        var createDTO = new ServiceProfileCreateDTO(
                "Specialty",
                "City",
                "ST",
                "url",
                "Time",
                "Desc"
        );

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            serviceProfileService.createServiceProfile(createDTO, userId);
        });

        // Verify
        verify(profileRepository, never())
                .save(any(ServiceProfile.class));
    }

    @Test
    @DisplayName("Should throw exception when user already has a profile")
    void createServiceProfile_WhenProfileAlreadyExists_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        var createDTO = new ServiceProfileCreateDTO(
                "Specialty",
                "City",
                "ST",
                "url",
                "Time",
                "Desc"
        );

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(profileRepository.existsByUserId(userId))
                .thenReturn(true);

        // When & Then
        assertThrows(ServiceProfileAlreadyExistsException.class, () -> {
            serviceProfileService.createServiceProfile(createDTO, userId);
        });

        // Verify
        verify(profileRepository, never())
                .save(any(ServiceProfile.class));
    }

    @Test
    @DisplayName("Should return ServiceProfileDTO when profile exists by User ID")
    void getServiceProfileByUserId_WhenProfileExists_ShouldReturnDTO() {
        // Given
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Location location = new Location(UUID.randomUUID(), "City", "ST");
        Specialty specialty = new Specialty(UUID.randomUUID(), "Specialty");

        ServiceProfile profile = new ServiceProfile();
        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        profile.setLocation(location);
        profile.setSpecialty(specialty);

        // Mocking
        when(profileRepository.findByUserId(userId))
                .thenReturn(Optional.of(profile));

        // When
        ServiceProfileResponseDTO result = serviceProfileService.getServiceProfileByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(profile.getId(), result.serviceProfileId());
        assertEquals(userId, result.userId());

        // Verify
        verify(profileRepository)
                .findByUserId(userId);
    }

    @Test
    @DisplayName("Should throw exception when profile not found by User ID")
    void getServiceProfileByUserId_WhenProfileDoesNotExist_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();

        // Mocking
        when(profileRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ServiceProfileNotFoundException.class, () -> {
            serviceProfileService.getServiceProfileByUserId(userId);
        });
    }

    @Test
    @DisplayName("Should return User ID when profile exists by Profile ID")
    void findUserIdByServiceProfileId_WhenProfileExists_ShouldReturnUserId() {
        // Given
        UUID profileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        ServiceProfile profile = new ServiceProfile();
        profile.setId(profileId);
        profile.setUser(user);

        // Mocking
        when(profileRepository.findById(profileId))
                .thenReturn(Optional.of(profile));

        // When
        UUID result = serviceProfileService.findUserIdByServiceProfileId(profileId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result);
    }

    @Test
    @DisplayName("Should throw exception when profile not found by Profile ID")
    void findUserIdByServiceProfileId_WhenProfileDoesNotExist_ShouldThrowException() {
        // Given
        UUID profileId = UUID.randomUUID();

        // Mocking
        when(profileRepository.findById(profileId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ServiceProfileNotFoundException.class, () -> {
            serviceProfileService.findUserIdByServiceProfileId(profileId);
        });
    }

    @Test
    @DisplayName("Should return list of all ServiceProfileDTOs")
    void getAllServiceProfilesDTO_ShouldReturnList() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        Location location = new Location(UUID.randomUUID(), "City", "ST");
        Specialty specialty = new Specialty(UUID.randomUUID(), "Spec");

        ServiceProfile profile1 = new ServiceProfile();
        profile1.setId(UUID.randomUUID());
        profile1.setUser(user);
        profile1.setLocation(location);
        profile1.setSpecialty(specialty);

        ServiceProfile profile2 = new ServiceProfile();
        profile2.setId(UUID.randomUUID());
        profile2.setUser(user);
        profile2.setLocation(location);
        profile2.setSpecialty(specialty);

        // Mocking
        when(profileRepository.findAll())
                .thenReturn(List.of(profile1, profile2));

        // When
        List<ServiceProfileResponseDTO> result = serviceProfileService.getAllServiceProfilesDTO();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify
        verify(profileRepository)
                .findAll();
    }
}
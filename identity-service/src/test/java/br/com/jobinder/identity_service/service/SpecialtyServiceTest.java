package br.com.jobinder.identity_service.service;

import br.com.jobinder.identityservice.domain.specialty.Specialty;
import br.com.jobinder.identityservice.domain.specialty.SpecialtyRepository;
import br.com.jobinder.identityservice.dto.specialty.SpecialtyDTO;
import br.com.jobinder.identityservice.service.SpecialtyService;
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
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    @DisplayName("Should return existing specialty when name already exists")
    void findOrCreateSpecialty_WhenSpecialtyExists_ShouldReturnExisting() {
        // Given
        String name = "Java Developer";
        Specialty existingSpecialty = new Specialty(UUID.randomUUID(), name);

        // Mocking
        when(specialtyRepository.findByNameIgnoreCase(name))
                .thenReturn(Optional.of(existingSpecialty));

        // When
        Specialty result = specialtyService.findOrCreateSpecialty(name);

        // Then
        assertNotNull(result);
        assertEquals(existingSpecialty.getId(), result.getId());
        assertEquals(name, result.getName());

        // Verify
        verify(specialtyRepository, never())
                .save(any(Specialty.class));
    }

    @Test
    @DisplayName("Should create and return new specialty when it does not exist")
    void findOrCreateSpecialty_WhenSpecialtyDoesNotExist_ShouldCreateNew() {
        // Given
        String name = "DevOps Engineer";

        // Mocking
        when(specialtyRepository.findByNameIgnoreCase(name))
                .thenReturn(Optional.empty());

        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> {
            Specialty spec = invocation.getArgument(0);
            spec.setId(UUID.randomUUID()); // Simulate ID generation
            return spec;
        });

        // When
        Specialty result = specialtyService.findOrCreateSpecialty(name);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(name, result.getName());

        // Verify
        verify(specialtyRepository)
                .save(any(Specialty.class));
    }

    @Test
    @DisplayName("Should return all specialties as DTOs")
    void findAll_ShouldReturnListDTO() {
        // Given
        Specialty s1 = new Specialty(UUID.randomUUID(), "Backend");
        Specialty s2 = new Specialty(UUID.randomUUID(), "Frontend");

        // Mocking
        when(specialtyRepository.findAll())
                .thenReturn(List.of(s1, s2));

        // When
        List<SpecialtyDTO> result = specialtyService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(s1.getName(), result.get(0).name());
        assertEquals(s2.getName(), result.get(1).name());

        // Verify
        verify(specialtyRepository)
                .findAll();
    }
}
package br.com.jobinder.identityservice.service;

import br.com.jobinder.identityservice.domain.specialty.Specialty;
import br.com.jobinder.identityservice.domain.specialty.SpecialtyRepository;
import br.com.jobinder.identityservice.dto.specialty.SpecialtyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    public Specialty findOrCreateSpecialty(String name) {
        return specialtyRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    var newSpecialty = new Specialty(null, name);
                    return specialtyRepository.save(newSpecialty);
                });
    }

    public List<SpecialtyDTO> findAll() {
        return specialtyRepository.findAll()
                .stream()
                .map(s -> new SpecialtyDTO(s.getId(), s.getName()))
                .toList();
    }
}
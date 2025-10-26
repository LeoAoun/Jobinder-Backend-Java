package br.com.jobinder.identityservice.controller;

import br.com.jobinder.identityservice.dto.specialty.SpecialtyDTO;
import br.com.jobinder.identityservice.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialties")
public class SpecialtyController {

    @Autowired
    private SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> getAllSpecialties() {
        var specialties = specialtyService.findAll();
        return ResponseEntity.ok(specialties);
    }
}
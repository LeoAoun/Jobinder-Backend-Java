package br.com.jobinder.identityservice.controller;

import br.com.jobinder.identityservice.domain.user.User;
import br.com.jobinder.identityservice.dto.user.UserCreateDTO;
import br.com.jobinder.identityservice.dto.user.UserResponseDTO;
import br.com.jobinder.identityservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserCreateDTO createDTO, UriComponentsBuilder uriBuilder) {
        var userResponse = userService.registerUser(createDTO);
        var uri = uriBuilder.path("/api/v1/users/{id}").buildAndExpand(userResponse.id()).toUri();
        return ResponseEntity.created(uri).body(userResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserDTOById(@PathVariable UUID id) {
        var userResponse = userService.findUserDTOById(id);
        return ResponseEntity.ok(userResponse);
    }

    /*
      Administrative Endpoints
      These endpoints are intended for administrative use only.
    */

    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        var user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }
}
package br.com.jobinder.identity_service.service;

import br.com.jobinder.identityservice.domain.user.User;
import br.com.jobinder.identityservice.domain.user.UserRepository;
import br.com.jobinder.identityservice.domain.user.UserRole;
import br.com.jobinder.identityservice.dto.internal.InternalUserAuthDTO;
import br.com.jobinder.identityservice.dto.user.UserChangePasswordDTO;
import br.com.jobinder.identityservice.dto.user.UserCreateDTO;
import br.com.jobinder.identityservice.dto.user.UserResponseDTO;
import br.com.jobinder.identityservice.dto.user.UserUpdateDTO;
import br.com.jobinder.identityservice.infra.exception.user.InvalidPasswordException;
import br.com.jobinder.identityservice.infra.exception.user.PhoneNumberInvalidException;
import br.com.jobinder.identityservice.infra.exception.user.UserAlreadyExistsException;
import br.com.jobinder.identityservice.infra.exception.user.UserNotFoundException;
import br.com.jobinder.identityservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should register user successfully when data is valid")
    void registerUser_WithValidData_ShouldSaveAndReturnDTO() {
        // Given
        var createDTO = new UserCreateDTO(
                "BR",
                "11999999999",
                "John",
                "Doe",
                "password123",
                UserRole.USER
        );
        String encodedPassword = "encodedPassword123";
        String formattedPhone = "+5511999999999";

        // Mocking
        when(userRepository.existsByPhone(formattedPhone))
                .thenReturn(false);
        when(passwordEncoder.encode(createDTO.password()))
                .thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID()); // Simulate ID generation
            return user;
        });

        // When
        UserResponseDTO response = userService.registerUser(createDTO);

        // Then
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(formattedPhone, response.phone());
        assertEquals(createDTO.firstName(), response.firstName());
        assertEquals(createDTO.lastName(), response.lastName());

        // Verify
        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when phone number is already registered")
    void registerUser_WithDuplicatePhone_ShouldThrowException() {
        // Given
        var createDTO = new UserCreateDTO(
                "BR",
                "11988888888",
                "Jane",
                "Doe",
                "password123",
                UserRole.USER
        );
        String formattedPhone = "+5511988888888";

        // Mocking
        when(userRepository.existsByPhone(formattedPhone))
                .thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(createDTO);
        });

        // Verify
        verify(userRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Should throw exception when phone number is invalid")
    void registerUser_WithInvalidPhone_ShouldThrowException() {
        // Given
        var createDTO = new UserCreateDTO(
                "BR",
                "Error",
                "Test",
                "Error",
                "error123",
                UserRole.USER
        );

        // When & Then
        assertThrows(PhoneNumberInvalidException.class, () -> {
            userService.registerUser(createDTO);
        });

        // Verify
        verify(userRepository, never())
                .existsByPhone(anyString());
    }

    @Test
    @DisplayName("Should return UserDTO when user exists by ID")
    void findUserDTOById_WhenUserExists_ShouldReturnDTO() {
        // Given
        UUID userId = UUID.randomUUID();
        String fistName = "Jane";
        String lastName = "Doe";

        User mockUser = new User(
                userId,
                "+5511999999999",
                fistName,
                lastName,
                "password123",
                UserRole.USER,
                null,
                null
        );

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(mockUser));

        // When
        UserResponseDTO result = userService.findUserDTOById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(fistName, result.firstName());
    }

    @Test
    @DisplayName("Should throw exception when user is not found by ID")
    void findUserDTOById_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserDTOById(userId);
        });
    }

    @Test
    @DisplayName("Should update user successfully when data is valid")
    void updateUser_WhenUserExists_ShouldUpdateAndReturnDTO() {
        // Given
        UUID userId = UUID.randomUUID();
        String newFirstName = "NewFistName";
        String newLastName = "NewLastName";

        var updateDTO = new UserUpdateDTO(
                newFirstName,
                newLastName
        );

        User existingUser = new User(
                userId,
                "+5511999999999",
                "OldFistName",
                "OldLastName",
                "password123",
                UserRole.USER,
                null,
                null
        );

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserResponseDTO result = userService.updateUser(userId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(newFirstName, result.firstName());
        assertEquals(newLastName, result.lastName());

        // Verify
        verify(userRepository)
                .save(existingUser);
    }

    @Test
    @DisplayName("Should throw exception when trying to update non-existent user")
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        var updateDTO = new UserUpdateDTO(
                "NewFirstName",
                "NewLastName"
        );

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updateDTO);
        });

        // Verify
        verify(userRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Should change password successfully when old password is correct")
    void changePassword_WhenOldPasswordIsCorrect_ShouldUpdatePassword() {
        // Given
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword456";
        String oldPassword = "oldPassword123";

        var passwordDTO = new UserChangePasswordDTO(
                oldPassword,
                newPassword
        );

        String oldHashedPassword = "$2to$10$oldHash";
        String newHashedPassword = "$2to$10$newHash";

        User user = new User();
        user.setId(userId);
        user.setPassword(oldHashedPassword);

        // Mocking
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, oldHashedPassword))
                .thenReturn(true);
        when(passwordEncoder.encode(newPassword))
                .thenReturn(newHashedPassword);

        // When
        userService.changePassword(userId, passwordDTO);

        // Then
        assertEquals(newHashedPassword, user.getPassword());

        // Verify
        verify(userRepository)
                .save(user);
    }

    @Test
    @DisplayName("Should throw exception when old password is incorrect")
    void changePassword_WhenOldPasswordIsIncorrect_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        String wrongOldPassword = "wrongOldPassword";
        String newPassword = "newPassword456";

        var passwordDTO = new UserChangePasswordDTO(
                wrongOldPassword,
                newPassword
        );

        String oldHashedPassword = "$2to$10$oldHash";

        User user = new User();
        user.setId(userId);
        user.setPassword(oldHashedPassword);

        // Mocking
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongOldPassword, oldHashedPassword)).thenReturn(false);

        // When & Then
        assertThrows(InvalidPasswordException.class, () -> {
            userService.changePassword(userId, passwordDTO);
        });

        assertEquals(oldHashedPassword, user.getPassword());
        verify(userRepository, never())
                .save(any());
    }


    @Test
    @DisplayName("Should return InternalUserAuthDTO when user exists by phone")
    void findAuthDetailsByPhone_WhenUserExists_ShouldReturnInternalDTO() {
        // Given
        String phone = "+5511999999999";
        User user = new User(
                UUID.randomUUID(),
                phone,
                "John",
                "Doe",
                "password123",
                UserRole.USER,
                null,
                null
        );

        // Mocking
        when(userRepository.findByPhone(phone))
                .thenReturn(Optional.of(user));

        // When
        InternalUserAuthDTO result = userService.findAuthDetailsByPhone(phone);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getPhone(), result.phone());
        assertEquals(user.getPassword(), result.hashedPassword());
        assertEquals("USER", result.role());
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void deleteUser_WhenUserExists_ShouldDelete() {
        // Given
        UUID userId = UUID.randomUUID();

        // Mocking
        when(userRepository.existsById(userId))
                .thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Verify
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete non-existent user")
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        // Verify
        verify(userRepository, never()).deleteById(any());
    }
}
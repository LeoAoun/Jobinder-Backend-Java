package br.com.jobinder.identityservice.service;

import br.com.jobinder.identityservice.domain.user.User;
import br.com.jobinder.identityservice.domain.user.UserRepository;
import br.com.jobinder.identityservice.dto.user.InternalUserAuthDTO;
import br.com.jobinder.identityservice.dto.user.UserCreateDTO;
import br.com.jobinder.identityservice.dto.user.UserResponseDTO;
import br.com.jobinder.identityservice.infra.exception.user.PhoneNumberInvalidException;
import br.com.jobinder.identityservice.infra.exception.user.UserAlreadyExistsException;
import br.com.jobinder.identityservice.infra.exception.user.UserNotFoundException;
import com.google.i18n.phonenumbers.NumberParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Thread-safe singleton instance of PhoneNumberUtil
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public UserResponseDTO registerUser(UserCreateDTO createDTO) {
        PhoneNumber phoneNumber;

        // Try to parse the phone number
        try {
            phoneNumber = phoneUtil.parse(createDTO.nationalNumber(), createDTO.countryCode());
        } catch (NumberParseException e) {
            throw new PhoneNumberInvalidException("Invalid phone number format: " + e.getMessage());
        }

        // Check if the number is valid for the given region
        if (!phoneUtil.isValidNumber(phoneNumber)) {
            throw new PhoneNumberInvalidException("Invalid phone number for the region " + createDTO.countryCode());
        }

        // Format the number to E.164 standard
        String e164FormattedPhone = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);

        // Check if a user with the same phone number already exists
        if (userRepository.existsByPhone(e164FormattedPhone)) {
            throw new UserAlreadyExistsException("A user with this phone number already exists.");
        }

        // Encode the password using BCrypt
        var encodedPassword = passwordEncoder.encode(createDTO.password());

        var user = new User(
                null,
                e164FormattedPhone,
                createDTO.firstName(),
                createDTO.lastName(),
                encodedPassword,
                createDTO.role(),
                null,
                null
        );

        // Save the user to the database
        var savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getPhone(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    public UserResponseDTO findUserDTOById(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return new UserResponseDTO(
                user.getId(),
                user.getPhone(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public InternalUserAuthDTO findAuthDetailsByPhone(String phone) {
        var user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException("User not found with phone: " + phone));

        return new InternalUserAuthDTO(
                user.getId(),
                user.getPhone(),
                user.getPassword(),
                user.getRole().name()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

}

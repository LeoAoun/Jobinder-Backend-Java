package br.com.jobinder.identityservice.service;

import br.com.jobinder.identityservice.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        // The username is the phone number, because we are using phone-based authentication
        return userRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone:" + phoneNumber));
    }
}
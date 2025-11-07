package com.laioffer.delivery.user;

import com.laioffer.delivery.common.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String email, String rawPassword) {
        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        if (normalizedEmail == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!StringUtils.hasText(rawPassword) || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }
        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordHash)
                .build();
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        String normalizedEmail = ValidationUtil.normalizeEmail(email);
        if (normalizedEmail == null) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(normalizedEmail);
    }

    public Optional<User> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return user;
    }
}

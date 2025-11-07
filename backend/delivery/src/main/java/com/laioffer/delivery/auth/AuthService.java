package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.User;
import com.laioffer.delivery.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public User register(String email, String password) {
        return userService.createUser(email, password);
    }

    public User authenticate(String email, String password) {
        return userService.authenticate(email, password);
    }

    public String createAccessToken(User user) {
        return jwtUtil.generateToken(user);
    }

    public Optional<User> findUser(UUID userId) {
        return userService.findById(userId);
    }
}

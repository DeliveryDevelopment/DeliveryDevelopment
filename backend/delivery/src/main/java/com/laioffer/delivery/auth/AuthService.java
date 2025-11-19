package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.User;
import com.laioffer.delivery.user.UserRole;
import com.laioffer.delivery.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * 注册逻辑
     */
    public User register(RegisterRequest request) {
        UserRole role = request.role() != null ? request.role() : UserRole.ROLE_USER;

        return userService.createUser(
                request.email(),
                request.password(),
                request.username(),
                role,
                request.phoneNumber()
        );
    }

    /**
     * 登录逻辑
     */
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        return jwtUtil.generateToken(user);
    }

    /**
     * 🔥 修复：把这个方法加回来，供 JwtFilter 使用
     */
    public User findUser(UUID userId) {
        return userService.findById(userId);
    }
}
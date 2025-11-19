package com.laioffer.delivery.user;

import com.laioffer.delivery.common.NotFoundException; // 假设你有这个异常类
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String email, String password, String username, UserRole role, String phone) {
        // 1. 检查 Email 是否重复
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // 2. 构建 User 对象
        User user = User.builder()
                .email(email)
                .username(username) // 新增
                .passwordHash(passwordEncoder.encode(password)) // 加密密码
                .role(role)         // 新增
                .phoneNumber(phone) // 新增
                .build();

        return userRepository.save(user);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    // 辅助方法：供 UserDetailsServiceImpl 使用
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }
}
package com.laioffer.delivery.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // 1. 用于登录：根据邮箱查找用户
    // 自动生成 SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // 2. 🔥 修复报错：用于注册：检查邮箱是否已存在
    // 自动生成 SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
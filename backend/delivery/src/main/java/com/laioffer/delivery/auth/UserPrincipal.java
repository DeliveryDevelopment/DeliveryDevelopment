package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// 必须实现 UserDetails 接口，Spring Security 才能识别
public class UserPrincipal implements UserDetails {

    // 持有完整的 User 实体对象
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    // 🔥 关键方法：OrderController 需要这个方法来获取 User ID 和 Entity
    public User getUser() {
        return user;
    }

    // --- 下面是 UserDetails 接口必须实现的方法 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将 UserRole (Enum) 转换为 Spring Security 认识的权限对象
        // 这样以后你可以用 @PreAuthorize("hasRole('ADMIN')")
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash(); // 验证密码时需要
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 这里的 Username 其实是 Email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 账号不过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 账号不锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 密码不过期
    }

    @Override
    public boolean isEnabled() {
        return true; // 账号可用
    }
}

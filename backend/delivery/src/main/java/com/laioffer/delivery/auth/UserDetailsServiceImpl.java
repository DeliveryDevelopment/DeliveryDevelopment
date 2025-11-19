package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.User;
import com.laioffer.delivery.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // 调用我们刚才在 UserService 里写的 findByEmail
            User user = userService.findByEmail(email);
            // 包装成 UserDetails 返回给 Spring Security
            return new UserPrincipal(user);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        UUID userId = null;

        // 1. 提取 Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            // 尝试提取 User ID (假设你的 JwtUtil 支持 extractUserId，如果报错请看下面的说明)
            userId = jwtUtil.extractUserId(token);
        }

        // 2. 验证并设置上下文
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 这里的 findUser 现在直接返回 User 对象，如果有问题它会自己抛异常
            // 🔥 修复：去掉了 .orElseThrow(...)
            User user = authService.findUser(userId);

            if (jwtUtil.validateToken(token, user)) {
                UserPrincipal userPrincipal = new UserPrincipal(user);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
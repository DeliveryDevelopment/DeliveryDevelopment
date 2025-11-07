package com.laioffer.delivery.auth;

import com.laioffer.delivery.common.ValidationUtil;
import com.laioffer.delivery.order.OrderService;
import com.laioffer.delivery.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final OrderService orderService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getEmail(), request.getPassword());
        return new RegisterResponse(user.getId(), user.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        String accessToken = authService.createAccessToken(user);

        String guestToken = extractGuestToken(httpRequest);
        if (StringUtils.hasText(guestToken)) {
            orderService.claimGuestOrders(guestToken, user.getId());
            clearGuestCookie(httpResponse);
        }

        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    private void clearGuestCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("guest_token", "")
                .path("/")
                .maxAge(Duration.ZERO)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractGuestToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("guest_token".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        public String getEmail() {
            return ValidationUtil.normalizeEmail(email);
        }
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;

        public String getEmail() {
            return ValidationUtil.normalizeEmail(email);
        }
    }

    public record RegisterResponse(UUID id, String email) {
    }

    public record LoginResponse(String accessToken) {
    }
}

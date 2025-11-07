package com.laioffer.delivery.auth;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public record RequestPrincipal(UUID userId, String guestToken) {

    public static RequestPrincipal from(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new RequestPrincipal(null, null);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return new RequestPrincipal(userPrincipal.id(), null);
        }
        if (principal instanceof GuestPrincipal guestPrincipal) {
            return new RequestPrincipal(null, guestPrincipal.guestToken());
        }
        return new RequestPrincipal(null, null);
    }

    public boolean isAuthenticatedUser() {
        return userId != null;
    }

    public boolean isGuest() {
        return guestToken != null;
    }
}

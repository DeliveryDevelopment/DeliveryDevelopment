package com.laioffer.delivery.auth;

import java.util.UUID;

public record UserPrincipal(UUID id, String email) {
}

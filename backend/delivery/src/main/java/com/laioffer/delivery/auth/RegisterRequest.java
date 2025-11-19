package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotBlank @Email
        String email,

        @NotBlank @Length(min = 6, message = "Password must be at least 6 chars")
        String password,

        @NotBlank
        String username, // 新增：必填

        UserRole role,   // 可选：如果不填，默认为 USER

        String phoneNumber // 可选
) {}
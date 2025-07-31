package com.ecommerce.ecommerce_from.jee.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String usernameOrEmail;
    private String password;
}

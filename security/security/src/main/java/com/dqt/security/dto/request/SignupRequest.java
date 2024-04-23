package com.dqt.security.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String address;
    private String identity;
    private String phone;
}

package com.dqt.security.dto.response;

import com.dqt.security.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String fullName;
    private String email;
    private String password;
    private String address;
    private String identity;
    private String phone;
    private Integer enable;
    Set<Role> roles;
}

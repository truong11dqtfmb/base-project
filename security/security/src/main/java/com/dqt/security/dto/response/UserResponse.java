package com.dqt.security.dto.response;

import com.dqt.security.entity.Role;
import com.dqt.security.entity.User;
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
    private String address;
    private String identity;
    private String phone;
    private Integer enable;
    Set<Role> roles;


    public UserResponse(User user){
        fullName = user.getFullName();
        email = user.getEmail();
        address = user.getAddress();
        identity = user.getIdentity();
        phone = user.getPhone();
        enable = user.getEnable();
        roles = user.getRoles();
    }
}

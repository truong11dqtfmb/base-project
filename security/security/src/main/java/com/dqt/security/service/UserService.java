package com.dqt.security.service;

import com.dqt.security.constant.Constant;
import com.dqt.security.dto.request.RoleRequest;
import com.dqt.security.dto.request.SignupRequest;
import com.dqt.security.dto.response.ResponseMessage;
import com.dqt.security.dto.response.UserResponse;
import com.dqt.security.entity.Role;
import com.dqt.security.entity.User;
import com.dqt.security.exception.ResourceNotFoundException;
import com.dqt.security.repository.RoleRepository;
import com.dqt.security.repository.UserRepository;
import com.dqt.security.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final Utils utils;

    public ResponseMessage signUp(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseMessage.error("User already registered");
        }

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Constant.ROLE_USER).orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "role"));
        roles.add(userRole);

        // Create new user's account
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .fullName(signUpRequest.getFullName())
                .identity(signUpRequest.getIdentity())
                .phone(signUpRequest.getPhone())
                .address(signUpRequest.getAddress())
                .password(encoder.encode(signUpRequest.getPassword()))
                .roles(roles)
                .createAt(new Date())
                .enable(Constant.ACTIVE)
                .build();
        User save = userRepository.save(user);
        return ResponseMessage.ok("Created new user", save);
    }

    public ResponseMessage changeRole(RoleRequest roleRequest) {
        Optional<User> optional = userRepository.findById(roleRequest.getUserId());
        if (!optional.isPresent()) {
            return ResponseMessage.error("User not found");
        }
        User user = optional.get();

        Set<Role> roles = new HashSet<>();

        roleRequest.getRoles().forEach(r -> {
            Role role = roleRepository.findByName(r.toUpperCase()).orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "role"));
            roles.add(role);
        });

        user.setRoles(roles);
        User save = userRepository.save(user);
        return ResponseMessage.ok("Change role user successfully", save);
    }

    public ResponseMessage getCurrentUser(){
        User user = utils.gerCurrentUser();
        if (user == null) {
            return ResponseMessage.error("User not found");
        }
        UserResponse userResponse = UserResponse.builder()
                .address(user.getAddress())
                .phone(user.getPhone())
                .identity(user.getIdentity())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enable(user.getEnable())
                .build();
        return ResponseMessage.ok("Get current user", userResponse);
    }


}

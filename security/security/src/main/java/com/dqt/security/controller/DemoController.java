package com.dqt.security.controller;

import com.dqt.security.constant.Authority;
import com.dqt.security.dto.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${api.prefix}/demo")

@Slf4j
@RequiredArgsConstructor
public class DemoController {

    @GetMapping("/test-admin")
    @PreAuthorize(Authority.ADMIN)
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok(ResponseMessage.ok("Hello Admin"));
    }

    @GetMapping("/test-user")
    @PreAuthorize(Authority.USER)
    public ResponseEntity<?> testUser() {
        return ResponseEntity.ok(ResponseMessage.ok("Hello User"));
    }

    @GetMapping("/test")
    @PreAuthorize(Authority.ADMIN_OR_USER)
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(ResponseMessage.ok("Hello Admin or User"));
    }

}

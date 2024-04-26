package com.dqt.security.controller;

import com.dqt.security.constant.Authority;
import com.dqt.security.constant.Constant;
import com.dqt.security.dto.request.RoleRequest;
import com.dqt.security.dto.request.UserRequest;
import com.dqt.security.dto.response.ResponseMessage;
import com.dqt.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("${api.prefix}/user")

@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("change-role")
    @PreAuthorize(Authority.ADMIN)
    public ResponseEntity<?> changeRole(@RequestBody RoleRequest roleRequest) {
        try {
            ResponseMessage responseMessage = userService.changeRole(roleRequest);
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("current-user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            ResponseMessage responseMessage = userService.getCurrentUser();
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> getListUser(@RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                         @RequestParam(required = false) String key) {
        try {
            log.info("Start getListUser with request: {}, {}, {}", page, pageSize, key);
            Map<String, Object> result = userService.getListUser(page, pageSize, key);
            log.info("End getListUser: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getListUser: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize(Authority.ADMIN)
    public ResponseEntity<?> addUser(@RequestBody UserRequest request) {
        try {
            log.info("Start addUser with request: {}", request);
            ResponseMessage responseMessage = userService.addUser(request);
            log.info("End addUser: {}", responseMessage);
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            log.error("Error addUser: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @PreAuthorize(Authority.ADMIN)
    public ResponseEntity<?> editUser(@RequestBody UserRequest request, @PathVariable Integer id) {
        try {
            log.info("Start editUser with request: {}", request);
            ResponseMessage responseMessage = userService.editUser(request, id);
            log.info("End editUser: {}", responseMessage);
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            log.error("Error editUser: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            log.info("Start getUserById with request: {}", id);
            ResponseMessage responseMessage = userService.getUserById(id);
            log.info("End getUserById: {}", responseMessage);
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            log.error("Error getUserById: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("enabled/{id}")
    @PreAuthorize(Authority.ADMIN)
    public ResponseEntity<?> enabledUser(@PathVariable Integer id) {
        try {
            log.info("Start enabledUser with request: {}", id);
            ResponseMessage responseMessage = userService.enabled(id, Constant.ACTIVE);
            log.info("End enabledUser: {}", responseMessage);
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            log.error("Error enabledUser: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("disabled/{id}")
    @PreAuthorize(Authority.ADMIN)
    public ResponseEntity<?> disabledUser(@PathVariable Integer id) {
        try {
            log.info("Start disabledUser with request: {}", id);
            ResponseMessage responseMessage = userService.enabled(id, Constant.INACTIVE);
            log.info("End disabledUser: {}", responseMessage);
            if (responseMessage.isStatus()) {
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
            return ResponseEntity.badRequest().body(responseMessage);
        } catch (Exception e) {
            log.error("Error disabledUser: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

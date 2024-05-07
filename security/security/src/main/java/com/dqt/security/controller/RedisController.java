package com.dqt.security.controller;

import com.dqt.security.entity.User;
import com.dqt.security.repository.UserRepository;
import com.dqt.security.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/redis")

@Slf4j
@RequiredArgsConstructor
public class RedisController {

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity set(@RequestParam String key, @RequestParam String value) {
        redisUtil.set(key, value, 1000L);
        return ResponseEntity.ok().body("Set OK");
    }

    @GetMapping
    public ResponseEntity get(@RequestParam String key) {
        String s = redisUtil.get(key, String.class);
        return ResponseEntity.ok().body(s);
    }

    @PostMapping("user")
    public ResponseEntity setUser(@RequestParam String key) {
        List<User> all = userRepository.findAll();
        redisUtil.set(key, all, 1000L);
        return ResponseEntity.ok().body("Set OK");
    }

    @GetMapping("user")
    public ResponseEntity getUser(@RequestParam String key) {
        User[] users = redisUtil.get(key, User[].class);
        return ResponseEntity.ok().body(users);
    }

}


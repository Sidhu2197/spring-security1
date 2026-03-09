package com.security.spring_security.Controller;

import com.security.spring_security.Model.User;
import com.security.spring_security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.register(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        Map<String, Object> response = new HashMap<>();
        if (authentication.isAuthenticated()) {
            User dbUser = userService.findByUsername(user.getUsername());
            if (dbUser == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            response.put("message", "Login successful!");
            response.put("username", dbUser.getUsername());
            response.put("age", dbUser.getAge());
            response.put("height", dbUser.getHeight());
            response.put("weight", dbUser.getWeight());
        } else {
            response.put("message", "Login failed!");
        }
        return response;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("age", user.getAge());
        profile.put("height", user.getHeight());
        profile.put("weight", user.getWeight());
        return profile;
    }
}

package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.Dto.UpdateUserProfileDto;
import com.bikeit.restendpoint.model.Dto.UserProfileDto;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("Ok!");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getAll());
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDto> getUserProfileByUsername(@PathVariable String username) {
        Optional<UserProfileDto> userProfile = userService.getUserProfileById(username);
        return userProfile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable String username, @RequestBody UpdateUserProfileDto updateUserProfileDto) {
        try {
            Optional<UserProfileDto> updatedProfile = userService.updateUserProfile(username, updateUserProfileDto);
            return updatedProfile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

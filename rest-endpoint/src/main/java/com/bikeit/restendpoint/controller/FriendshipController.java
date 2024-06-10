package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.UserRepository;
import com.bikeit.restendpoint.service.FriendshipService;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bikeit.restendpoint.model.Dto.FriendUsernameDto;

import java.util.Set;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/friends")
    public ResponseEntity<Set<User>> getFriends() {
        try {
            String username = userService.getCurrentUsername();
            User currentUser = userRepository.findByUsername(username);
            Set<User> friends = friendshipService.getFriends(currentUser);
            return ResponseEntity.ok().body(friends);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/unfriend")
    public ResponseEntity<?> deleteFriend(@RequestBody FriendUsernameDto friendUsernameDto) {
        try {
            String currentUsername = userService.getCurrentUsername();
            User currentUser = userRepository.findByUsername(currentUsername);
            User friend = userRepository.findByUsername(friendUsernameDto.getUsername());
            if(!friendshipService.deleteFriends(currentUser, friend)) throw new IllegalArgumentException("Friend deletion failed");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
}

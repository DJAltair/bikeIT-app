package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import com.bikeit.restendpoint.service.FriendRequestService;
import com.bikeit.restendpoint.service.FriendshipService;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/friendships")
public class FriendRequestController {
    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private FriendshipRepository friendshipRepository;

    @GetMapping("/befriend/{username}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String username) {
        try {
            String currentUsername = userService.getCurrentUsername();
            User sender = userRepository.findByUsername(currentUsername);
            User receiver = userRepository.findByUsername(username);

            if(receiver == null) throw new IllegalArgumentException("Receiver doesn't exist!");

            friendRequestService.sendFriendRequest(sender, receiver);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/accept-friend-request/{username}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable String username) {
        try {
            String currentUsername = userService.getCurrentUsername();
            User receiver = userRepository.findByUsername(currentUsername);
            User sender = userRepository.findByUsername(username);

            Set<User> potentialFriends = friendRequestService.getPotentialFriends(receiver);
            if(!potentialFriends.contains(receiver)) throw new IllegalArgumentException("Sender not a potential friend!");
            friendRequestService.acceptFriendRequest(sender, receiver);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/deny-friend-request/{username}")
    public ResponseEntity<?> declineFriendRequest(@PathVariable String username) {
        try {
            String currentUsername = userService.getCurrentUsername();
            User receiver = userRepository.findByUsername(currentUsername);
            User sender = userRepository.findByUsername(username);

            Set<User> potentialFriends = friendRequestService.getPotentialFriends(receiver);
            if(!potentialFriends.contains(receiver)) throw new IllegalArgumentException("Sender not a potential friend!");
            friendRequestService.acceptFriendRequest(sender, receiver);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/friend-requests")
    public ResponseEntity<Set<User>> showFriendRequests() {

        String username = userService.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username);

        Set<User> potentialFriends = friendRequestService.getPotentialFriends(currentUser);
        return ResponseEntity.ok().body(potentialFriends);
    }
}
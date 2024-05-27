package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.Dto.FriendChoiceDto;
import com.bikeit.restendpoint.model.Dto.FriendRequestDto;
import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import com.bikeit.restendpoint.service.FriendRequestService;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {
    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        try {
            Long senderId = friendRequestDto.getSenderId();
            Long receiverId = friendRequestDto.getReceiverId();

            Optional<User> sender = userRepository.findById(senderId);
            Optional<User> receiver = userRepository.findById(receiverId);

            if(sender.isEmpty() || receiver.isEmpty()) throw new IllegalArgumentException("Sender or receiver doesn't exist!");

            String username = userService.getCurrentUsername();
            User currentUser = userRepository.findByUsername(username);

            if (!sender.get().getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Current user isn't the sender!");
            }

            friendRequestService.sendFriendRequest(sender.get(), receiver.get());
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendChoiceDto friendChoiceDto) {
        try {
            Long friendshipId = friendChoiceDto.getFriendshipId();

            String username = userService.getCurrentUsername();
            User currentUser = userRepository.findByUsername(username);

            friendRequestService.acceptFriendRequest(currentUser, friendshipId);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/decline")
    public ResponseEntity<?> declineFriendRequest(@RequestBody FriendChoiceDto friendChoiceDto) {
        try {
            Long friendshipId = friendChoiceDto.getFriendshipId();

            String username = userService.getCurrentUsername();
            User currentUser = userRepository.findByUsername(username);

            friendRequestService.declineFriendRequest(currentUser, friendshipId);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/show")
    public ResponseEntity<List<Friendship>> showFriendRequests() {

        String username = userService.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username);

        List<Friendship> friendRequests = friendRequestService.getFriendRequests(currentUser);
        return ResponseEntity.ok().body(friendRequests);
    }
}
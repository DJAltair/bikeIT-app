package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.exceptions.FriendshipNotFoundException;
import com.bikeit.restendpoint.exceptions.UserNotFoundException;
import com.bikeit.restendpoint.model.Dto.FriendChoiceDto;
import com.bikeit.restendpoint.model.Dto.FriendRequestDto;
import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import com.bikeit.restendpoint.service.FriendRequestService;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {
    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        Long senderId = friendRequestDto.getSenderId();
        Long receiverId = friendRequestDto.getReceiverId();

        User sender = userRepository.findById(senderId).orElseThrow(() -> new UserNotFoundException(senderId));

        String username = userService.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username);

        if (!sender.getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new UserNotFoundException(receiverId));

        friendRequestService.sendFriendRequest(sender, receiver);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendChoiceDto friendChoiceDto) {
        Long friendshipId = friendChoiceDto.getFriendshipId();

        String username = userService.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username);

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException(friendshipId));

        friendRequestService.acceptFriendRequest(currentUser, friendshipId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline")
    public ResponseEntity<?> declineFriendRequest(@RequestBody FriendChoiceDto friendChoiceDto) {
        Long friendshipId = friendChoiceDto.getFriendshipId();

        String username = userService.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username);

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException(friendshipId));

        friendRequestService.declineFriendRequest(currentUser, friendshipId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/show")
    public ResponseEntity<List<Friendship>> showFriendRequests() {

        String username = userService.getCurrentUsername();
        User currentUser = userRepository.findByUsername(username);

        List<Friendship> friendRequests = friendRequestService.getFriendRequests(currentUser);
        return ResponseEntity.ok().body(friendRequests);
    }
}
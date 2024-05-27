package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.exceptions.FriendshipNotFoundException;
import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.FriendshipStatus;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestService {
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendFriendRequest(User sender, User receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same user");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING)) {
            throw new IllegalStateException("Friend request already sent");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.ACCEPTED)) {
            throw new IllegalStateException("You are already friends");
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    public void acceptFriendRequest(User receiver, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException(friendshipId));

        if (!friendship.getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("Only the receiver can accept friend requests");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        User sender = friendship.getSender();

        if (!receiver.getFriends().contains(sender)) {
            receiver.getFriends().add(sender);
            userRepository.save(receiver);
        }

        if (!sender.getFriends().contains(receiver)) {
            sender.getFriends().add(receiver);
            userRepository.save(sender);
        }
    }

    public void declineFriendRequest(User receiver, Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException(friendshipId));

        // Check if the receiver matches the receiver of the friendship request
        if (!friendship.getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("Only the receiver can decline friend requests");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
    }

    public List<Friendship> getFriendRequests(User receiver) {
        return friendshipRepository.findBySenderOrReceiverAndStatus(null, receiver, FriendshipStatus.PENDING);
    }
}

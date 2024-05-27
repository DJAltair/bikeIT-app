package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.FriendshipStatus;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendFriendRequest(User sender, User receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same user!");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING)) {
            throw new IllegalStateException("Friend request already sent!");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.ACCEPTED)) {
            throw new IllegalStateException("You are already friends!");
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    public void acceptFriendRequest(User receiver, Long friendshipId) {
        Optional<Friendship> friendship = friendshipRepository.findById(friendshipId);

        if(friendship.isEmpty()) throw new IllegalArgumentException("Friend request doesn't exist!");

        if (!friendship.get().getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("Only the receiver can accept friend requests!");
        }

        if (friendship.get().getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Cannot accept a non-pending friend request!");
        }

        friendship.get().setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship.get());

        User sender = friendship.get().getSender();

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
        Optional<Friendship> friendship = friendshipRepository.findById(friendshipId);

        if(friendship.isEmpty()) throw new IllegalArgumentException("Friend request doesn't exist!");

        if (!friendship.get().getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("Only the receiver can decline friend requests");
        }

        if (friendship.get().getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Cannot decline a non-pending friend request!");
        }

        friendship.get().setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship.get());
    }

    public List<Friendship> getFriendRequests(User receiver) {
        return friendshipRepository.findBySenderOrReceiverAndStatus(null, receiver, FriendshipStatus.PENDING);
    }
}

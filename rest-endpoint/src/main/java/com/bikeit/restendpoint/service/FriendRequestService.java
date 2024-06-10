package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.FriendshipStatus;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            throw new IllegalStateException("Friend request already sent! You sent the request.");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(receiver, sender, FriendshipStatus.PENDING)) {
            throw new IllegalStateException("Friend request already sent! You received the request.");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.ACCEPTED)) {
            throw new IllegalStateException("You are already friends! You sent the request.");
        }

        if (friendshipRepository.existsBySenderAndReceiverAndStatus(receiver, sender, FriendshipStatus.ACCEPTED)) {
            throw new IllegalStateException("You are already friends! You received the request.");
        }

        Friendship friendship = new Friendship(sender, receiver, FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void acceptFriendRequest(User sender, User receiver) {
        List<Friendship> friendships = friendshipRepository.findBySenderOrReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING);
        if (friendships.size() != 1) {
            throw new IllegalArgumentException("Friend request doesn't exist!");
        }
        Friendship friendship = friendships.get(0);
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Cannot accept a non-pending friend request!");
        }
        friendshipRepository.updateStatus(friendship.getId(), FriendshipStatus.ACCEPTED);
    }

    @Transactional
    public void declineFriendRequest(User sender, User receiver) {
        List<Friendship> friendships = friendshipRepository.findBySenderOrReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING);
        if (friendships.size() != 1) {
            throw new IllegalArgumentException("Friend request doesn't exist!");
        }
        Friendship friendship = friendships.get(0);
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Cannot decline a non-pending friend request!");
        }
        friendshipRepository.delete(friendship);
    }

    public Set<User> getPotentialFriends(User user) {
        Set<User> friends = friendshipRepository.findPendingFriendsForUser(user, FriendshipStatus.PENDING);
        friends.remove(user);
        return friends;
    }
}

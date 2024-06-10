package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.FriendshipStatus;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepository;

    public Set<User> getFriends(User user) {
        Set<User> friends = friendshipRepository.findAcceptedFriendsOfUser(user, FriendshipStatus.ACCEPTED);
        friends.remove(user);
        return friends;
    }

    public boolean deleteFriends(User user, User friend) {
        Friendship friendship = friendshipRepository.findFriendshipByUsers(user, friend);
        if(friendship == null) return false;
        friendshipRepository.delete(friendship);
        return true;
    }
}
package com.bikeit.restendpoint.service;

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
        return user.getFriends();
    }
}
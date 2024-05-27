package com.bikeit.restendpoint.exceptions;

public class FriendshipNotFoundException extends RuntimeException {
    public FriendshipNotFoundException(Long friendshipId) {
        super("Friendship not found with id: " + friendshipId);
    }
}

package com.bikeit.restendpoint.repository;

import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.FriendshipStatus;
import com.bikeit.restendpoint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findBySenderOrReceiverAndStatus(User user1, User user2, FriendshipStatus status);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendshipStatus status);
}

package com.bikeit.restendpoint.repository;

import com.bikeit.restendpoint.model.Friendship;
import com.bikeit.restendpoint.model.FriendshipStatus;
import com.bikeit.restendpoint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findBySenderOrReceiverAndStatus(User user1, User user2, FriendshipStatus status);
    Friendship findFriendshipById(Long id);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendshipStatus status);
    @Query("SELECT u FROM User u JOIN Friendship f ON (u = f.sender OR u = f.receiver) WHERE (f.receiver = :user OR f.sender = :user) AND f.status = :status")
    Set<User> findAcceptedFriendsOfUser(@Param("user") User user, @Param("status") FriendshipStatus status);
    @Query("SELECT f.sender FROM Friendship f WHERE f.receiver = :user AND f.status = :status")
    Set<User> findPendingFriendsForUser(@Param("user") User user, @Param("status") FriendshipStatus status);
    @Query("SELECT f FROM Friendship f WHERE (:user1 = f.sender AND :user2 = f.receiver) OR (:user1 = f.receiver AND :user2 = f.sender)")
    Friendship findFriendshipByUsers(@Param("user1") User user1, @Param("user2") User user2);
    @Modifying
    @Query("UPDATE Friendship f SET f.status = :status WHERE f.id = :friendshipId")
    void updateStatus(@Param("friendshipId") Long friendshipId, @Param("status") FriendshipStatus status);
}

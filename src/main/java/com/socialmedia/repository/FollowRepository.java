package com.socialmedia.repository;

import com.socialmedia.entity.Follow;
import com.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    boolean existsByFollowerAndFollowee(User follower, User followee);

    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    @Query(value = "SELECT * FROM users usr" +
            " JOIN follows flw ON usr.id= flw.follower_id" +
            " WHERE usr.id= %:userId%" +
            " AND flw.status= %:statue%" +
            " ORDER BY flw.created_at", nativeQuery = true)
    List<User> getFollowers(@Param("userId") int userId, @Param("statue") String statue);

    @Query( value = "SELECT * FROM users usr" +
            " JOIN follows flw ON usr.id= flw.followee_id" +
            " WHERE usr.id= %:userId%" +
            " AND flw.status= %:statue%" +
            " ORDER BY flw.created_at", nativeQuery = true)
    List<User> getFollowees(@Param("userId") int userId, @Param("statue") String statue);
}

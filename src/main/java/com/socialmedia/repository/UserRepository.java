package com.socialmedia.repository;

import com.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPasswordToken(String token);

    @Query(value = "SELECT * FROM users usr" +
            " JOIN regulars rgl ON rgl.user_id = usr.id" +
            " WHERE usr.username LIKE %:input%" +
            " OR rgl.first_name LIKE %:input%" +
            " OR rgl.last_name LIKE %:input%" +
            " ORDER BY usr.username", nativeQuery = true)
    List<User> searchUsersByUsernameAndName(@Param("input") String input);
}

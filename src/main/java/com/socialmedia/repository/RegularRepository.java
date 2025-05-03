package com.socialmedia.repository;

import com.socialmedia.entity.Regular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegularRepository extends JpaRepository<Regular, Integer> {

    @Query(value = "SELECT * FROM regulars rgl" +
            " JOIN users usr ON rgl.user_id = usr.id" +
            " WHERE usr.username LIKE %:input%" +
            " OR rgl.first_name LIKE %:input%" +
            " OR rgl.last_name LIKE %:input%" +
            " ORDER BY usr.username", nativeQuery = true)
    List<Regular> searchRegularsByUsernameAndName(@Param("input") String input);
}

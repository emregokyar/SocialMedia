package com.socialmedia.repository;

import com.socialmedia.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {

    @Query(value = "SELECT DISTINCT pht.id, pht.caption, pht.location, pht.extension, pht.registration_date, pht.user_id FROM photos pht" +
            " JOIN users usr ON usr.id != pht.user_id" +
            " JOIN follows flw ON usr.id = flw.follower_id" +
            " LEFT JOIN likes lk ON lk.photo_id = pht.id AND lk.user_id= %:userId%" +
            " WHERE usr.id = %:userId%" +
            " AND flw.status = %:statue%" +
            " AND lk.id IS NULL " +
            " AND (DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date)) < 7" +
            " ORDER BY pht.registration_date DESC",
            nativeQuery = true)
    List<Photo> getFollowingPosts(@Param("userId") int userId, @Param("statue") String statue);
    //Retrieving following photos which user hasn't liked yet in the last seven days

    @Query(value = "SELECT pht.id, pht.caption, pht.location, pht.extension, pht.registration_date, pht.user_id, COUNT(lks.id) as like_count " +
            "FROM photos pht " +
            "LEFT JOIN likes lks ON pht.id = lks.photo_id " +
            "JOIN users usr ON usr.id = pht.user_id " +
            "WHERE usr.is_private = 0 " +
            "AND pht.user_id != :userId " +
            "AND pht.id NOT IN (SELECT photo_id FROM likes WHERE user_id = :userId) " +
            "AND DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date) < :day " +
            "GROUP BY pht.id " +
            "ORDER BY like_count DESC",
            nativeQuery = true)
    List<Photo> getMostLikedPhotos(@Param("userId") int userId, @Param("day") int day);
    //Retrieving most liked photos user hasn't liked yet

    @Query(value = "SELECT DISTINCT pht.id, pht.caption, pht.location, pht.extension, pht.registration_date, pht.user_id" +
            " FROM photos pht" +
            " JOIN likes lk ON lk.photo_id = pht.id" +
            " WHERE lk.user_id = %:userId%" +
            " AND (DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date)) < 7" +
            " ORDER BY pht.registration_date DESC",
            nativeQuery = true)
    List<Photo> getLikedPosts(@Param("userId") int userId, @Param("statue") String statue);
    //Retrieving following photos which user liked yet in the last seven days

    @Query(value = "SELECT DISTINCT pht.id, pht.caption, pht.location, pht.extension, pht.registration_date, pht.user_id" +
            " FROM photos pht" +
            " JOIN likes lk ON lk.photo_id = pht.id" +
            " WHERE lk.user_id = :userId" +
            " AND DATEDIFF(CURRENT_TIMESTAMP, pht.registration_date) < 7" +
            " ORDER BY pht.registration_date DESC",
            nativeQuery = true)
    List<Photo> getLikedPosts(@Param("userId") int userId);

}

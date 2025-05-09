package com.socialmedia.repository;

import com.socialmedia.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {

    @Query(value = "SELECT DISTINCT pht.*" +
            " FROM photos pht" +
            " JOIN users usr ON usr.id != pht.user_id" +
            " JOIN follows flw ON usr.id = flw.follower_id" +
            " LEFT JOIN likes lk ON lk.photo_id = pht.id AND lk.user_id= :userId" +
            " WHERE usr.id = :userId" +
            " AND flw.status = :statue" +
            " AND lk.id IS NULL " +
            " AND (DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date)) < 7" +
            " ORDER BY pht.registration_date DESC",
            nativeQuery = true)
    List<Photo> getFollowingPosts(@Param("userId") int userId, @Param("statue") String statue);
    //Retrieving following photos which user hasn't liked yet in the last seven days

    @Query(value = "SELECT pht.* " +
            "FROM photos pht " +
            "LEFT JOIN likes lks ON pht.id = lks.photo_id " +
            "JOIN users usr ON usr.id = pht.user_id " +
            "WHERE usr.is_private = 0 " +
            "AND pht.user_id != :userId " +
            "AND pht.id NOT IN (SELECT photo_id FROM likes WHERE user_id = :userId) " +
            "AND DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date) < :day " +
            "GROUP BY pht.id " +
            "ORDER BY COUNT(lks.id) DESC",
            nativeQuery = true)
    List<Photo> getMostLikedPhotos(@Param("userId") int userId, @Param("day") int day);
    //Retrieving most liked photos user hasn't liked yet

    @Query(value = "SELECT DISTINCT pht.*" +
            " FROM photos pht" +
            " JOIN likes lk ON lk.photo_id = pht.id" +
            " JOIN follows flw ON flw.followee_id = pht.user_id" +
            " WHERE lk.user_id = :userId" +
            " AND DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date) < 7" +
            " AND flw.status = :status" +
            " ORDER BY pht.registration_date DESC",
            nativeQuery = true)
    List<Photo> getLikedPosts(@Param("userId") int userId, @Param("status") String status);

    @Query(value = "SELECT DISTINCT pht.*" +
            " FROM photos pht" +
            " LEFT JOIN photo_tags tgs ON tgs.photo_id = pht.id" +
            " LEFT JOIN universal_tags unitgs ON unitgs.id = tgs.universal_tag_id" +
            " LEFT JOIN likes lk ON lk.photo_id = pht.id" +
            " LEFT JOIN users usr ON pht.user_id = usr.id" +
            " WHERE (pht.location LIKE CONCAT('%', :input, '%')" +
            " OR unitgs.tag_name LIKE CONCAT('%', :input, '%'))" +
            " AND pht.user_id != :userId" +
            " AND usr.is_private = FALSE" +
            " AND (DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date)) < 30" +
            " GROUP BY pht.id" +
            " ORDER BY COUNT(lk.id) DESC",
            nativeQuery = true)
    List<Photo> findByUniversalTag(@Param("input") String input, @Param("userId") int userId);
    //Retrieving all the photos by tag name or location

    @Query(value = "SELECT DISTINCT pht.*" +
            " FROM photos pht" +
            " JOIN follows flw ON flw.followee_id = pht.user_id" +
            " JOIN users usr ON usr.id = pht.user_id" +
            " LEFT JOIN photo_tags tgs ON tgs.photo_id = pht.id" +
            " LEFT JOIN universal_tags unitgs ON unitgs.id = tgs.universal_tag_id" +
            " WHERE flw.follower_id = :userId" +
            " AND (pht.location LIKE CONCAT('%', :input, '%')" +
            " OR unitgs.tag_name LIKE CONCAT('%', :input, '%'))" +
            " AND flw.status = :statue" +
            " AND (DATEDIFF(CURRENT_TIMESTAMP(), pht.registration_date)) < 365" +
            " ORDER BY pht.registration_date DESC",
            nativeQuery = true)
    List<Photo> searchFromFollowingPost(@Param("userId") int userId, @Param("statue") String statue, @Param("input") String input);

}

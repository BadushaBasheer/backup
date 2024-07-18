package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Image, Long> {

    @Query("SELECT p FROM Image p WHERE p.user.id =:userId")
    List<Image> findPostByUserId(@Param("userId") Long userId);

}

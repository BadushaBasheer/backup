package com.socialmedia.socialmedia.services.post;


import com.socialmedia.socialmedia.entities.Image;

import java.util.List;

public interface PostService {

    List<Image> findAllPosts();

    List<Image> findPostByUserId(Long userId);

    Image findPostById(Long postId) throws Exception;

    Image createNewPost(Image image, Long userId) throws Exception;

    Image likePost(Long postId, Long userId) throws Exception;

    Image commentPost(Long postId, Long userId) throws Exception;

    Image savedPost(Long postId, Long userId) throws Exception;

    String deletePost(Long postId, Long userId) throws Exception;
}

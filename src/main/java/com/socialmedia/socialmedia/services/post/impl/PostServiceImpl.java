package com.socialmedia.socialmedia.services.post.impl;

import com.socialmedia.socialmedia.entities.Image;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.repositories.PostRepository;
import com.socialmedia.socialmedia.services.jwt.UserService;
import com.socialmedia.socialmedia.services.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserService userService;

    private final PostRepository postRepository;

    @Override
    public List<Image> findAllPosts() {
        try {
            return postRepository.findAll();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch all posts", ex);
        }
    }

    @Override
    public Image findPostById(Long postId) {
        Optional<Image> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new NoSuchElementException("Post not found with id : " + postId);
        }
        return post.get();
    }

    @Override
    public List<Image> findPostByUserId(Long userId) {
        return postRepository.findPostByUserId(userId);
    }

    @Override
    public Image createNewPost(Image image, Long userId) {
        User user = userService.findUserById(userId);
        if (user != null) {
            Image image1 = new Image();
            image1.setUser(user);
            image1.setImageUrl(image.getImageUrl());
            image1.setCaption(image.getCaption());
            image1.setCreatedAt(LocalDateTime.now());
            return postRepository.save(image1);
        }
        else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Override
    public Image savedPost(Long postId, Long userId) {
        Image image = findPostById(postId);
        User user = userService.findUserById(userId);
        if (image == null || user == null) {
            return null;
        }
        List<Image> savedImages = user.getSavedImage();

        if (savedImages.contains(image)) {
            savedImages.remove(image);
        }
        else {
            savedImages.add(image);
        }
        user.setSavedImage(savedImages);
        userService.updateUser(user);
        return image;
    }

    @Override
    public Image likePost(Long postId, Long userId) {
        Image image = findPostById(postId);
        User user = userService.findUserById(userId);
        List<User> likedUsers = image.getLiked();
        if (likedUsers == null) {
            likedUsers = new ArrayList<>();
        }
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
        }
        else {
            likedUsers.add(user);
        }
        image.setLiked(likedUsers);
        return postRepository.save(image);
    }

    @Override
    public Image commentPost(Long postId, Long userId) throws Exception {
        Image image = findPostById(postId);
        User user = userService.findUserById(userId);

        return null;
    }

    @Override
    public String deletePost(Long postId, Long userId) {
        Image image = findPostById(postId);
        User user = userService.findUserById(userId);
        if (!Objects.equals(image.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You can't delete another user's post");
        }
        postRepository.deleteById(postId);
        return "Post with ID " + postId + " has been deleted successfully";
    }

}


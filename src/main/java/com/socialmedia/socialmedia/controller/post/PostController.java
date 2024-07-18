package com.socialmedia.socialmedia.controller.post;

import com.socialmedia.socialmedia.entities.Image;
import com.socialmedia.socialmedia.response.ApiResponse;
import com.socialmedia.socialmedia.services.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Image>> findAllPost() {
        try {
            List<Image> images = postService.findAllPosts();
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Image> createPost(@RequestBody Image image, @PathVariable Long userId) throws Exception {
        Image createdImage = postService.createNewPost(image, userId);
        return new ResponseEntity<>(createdImage, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Image>> findUsersPost(@PathVariable Long userId) {
        List<Image> images = postService.findPostByUserId(userId);
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Image> findPostByIdHandler(@PathVariable Long postId) throws Exception {
        Image image = postService.findPostById(postId);
        return new ResponseEntity<>(image, HttpStatus.ACCEPTED);
    }

    @PutMapping("/save/{postId}/user/{userId}")
    public ResponseEntity<Image> savePostByIdHandler(@PathVariable Long postId, @PathVariable Long userId) throws Exception {
        Image image = postService.savedPost(postId, userId);
        return new ResponseEntity<>(image, HttpStatus.ACCEPTED);
    }

    @PutMapping("/like/{postId}/user/{userId}")
    public ResponseEntity<Image> likePostByIdHandler(@PathVariable Long postId, @PathVariable Long userId) throws Exception {
        Image image = postService.likePost(postId, userId);
        return new ResponseEntity<>(image, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{postId}/user/{userId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        try {
            String message = postService.deletePost(postId, userId);
            ApiResponse response = new ApiResponse(message, true);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            String errorMessage = "Failed to delete the post: " + ex.getMessage();
            ApiResponse errorResponse = new ApiResponse(errorMessage, false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

//    ---------------------------------------------------------------------



}

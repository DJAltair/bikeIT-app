package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.Dto.PostDto;
import com.bikeit.restendpoint.model.Post;
import com.bikeit.restendpoint.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        Optional<PostDto> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/posts/{username}")
    public List<PostDto> getPostsByUsername(@PathVariable String username) {
        return postService.getPostsByUsername(username);
    }

    @PostMapping("/post")
    public ResponseEntity<PostDto> createPost(@RequestBody Post post) {
        try {
            PostDto createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        try {
            PostDto updatedPost = postService.updatePost(id, postDetails);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
}

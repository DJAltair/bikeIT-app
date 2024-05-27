package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Dto.PostDto;
import com.bikeit.restendpoint.model.Post;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.PostRepository;
import com.bikeit.restendpoint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PostDto> getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDto);
    }

    public PostDto createPost(Post post) {
        String username = userService.getCurrentUsername();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    public PostDto updatePost(Long id, Post postDetails) {
        String username = userService.getCurrentUsername();
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUser().getUsername().equals(username)) {
                throw new SecurityException("You are not authorized to update this post");
            }
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            Post updatedPost = postRepository.save(post);
            return convertToDto(updatedPost);
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }

    public void deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (RoleService.hasRole("ROLE_ADMIN") || post.getUser().getUsername().equals(userService.getCurrentUsername())) {
                postRepository.delete(post);
            } else {
                throw new SecurityException("You are not authorized to delete this post");
            }
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }

    private PostDto convertToDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUser().getUsername()
        );
    }
}

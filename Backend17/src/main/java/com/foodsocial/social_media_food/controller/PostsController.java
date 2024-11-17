package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.Post;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.PostRepository;
import com.foodsocial.social_media_food.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostsController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((User) auth.getPrincipal()).getId();
        post.setUserId(userId); // Устанавливаем userId текущего пользователя
        Post createdPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        if (post.getId() == null || !postRepository.existsById(post.getId())) {
            return ResponseEntity.notFound().build();
        }
        post.setId(id);
        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/our_posts")
    public ResponseEntity<List<Post>> getCurrentUserPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((User) auth.getPrincipal()).getId();
        List<Post> posts = postRepository.findByUserId(userId);
        return ResponseEntity.ok(posts);
    }
}


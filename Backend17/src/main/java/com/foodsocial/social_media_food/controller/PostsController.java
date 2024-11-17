package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.security.ForbiddenException;
import com.foodsocial.social_media_food.security.NotFoundException;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import com.foodsocial.social_media_food.service.CookieService;
import com.foodsocial.social_media_food.domain.Post;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.PostRepository;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/posts")
public class PostsController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CookieService cookieService;

    private static final Logger logger = Logger.getLogger(PostsController.class.getName());

    private User getAuthenticatedUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    try {
                        User user = cookieService.ifAuthUser(cookie.getValue());
                        return user;
                    } catch (UnauthorizedException e) {
                        throw new UnauthorizedException("Invalid token provided");
                    }
                }
            }
        }
        throw new UnauthorizedException("No valid token found");
    }



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
    public ResponseEntity<Post> createPost(@RequestBody Post post, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        post.setUserId(user.getId());
        Post createdPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        Optional<Post> existingPost = postRepository.findById(id);

        if (!existingPost.isPresent()) {
            throw new NotFoundException("Post not found");
        }

        Post existingPostEntity = existingPost.get();
        if (!existingPostEntity.getUserId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            throw new ForbiddenException("Access denied");
        }

        post.setId(id);
        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        Optional<Post> existingPost = postRepository.findById(id);

        if (!existingPost.isPresent()) {
            throw new NotFoundException("Post not found");
        }

        Post existingPostEntity = existingPost.get();
        if (!existingPostEntity.getUserId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            throw new ForbiddenException("Access denied");
        }

        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/our_posts")
    public ResponseEntity<List<Post>> getCurrentUserPosts(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        List<Post> posts = postRepository.findByUserId(user.getId());
        return ResponseEntity.ok(posts);
    }
}

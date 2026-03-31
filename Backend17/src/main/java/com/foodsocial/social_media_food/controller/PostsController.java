package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.PostComment;
import com.foodsocial.social_media_food.domain.PostLike;
import com.foodsocial.social_media_food.repos.PostCommentRepository;
import com.foodsocial.social_media_food.security.ForbiddenException;
import com.foodsocial.social_media_food.security.NotFoundException;
import com.foodsocial.social_media_food.service.CookieService;
import com.foodsocial.social_media_food.domain.Post;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.PostRepository;
import com.foodsocial.social_media_food.repos.PostLikeRepository;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "API for working with posts")
public class PostsController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CookieService cookieService;

    @Value("${upload.path}")
    private String uploadPath;

    private static final Logger logger = Logger.getLogger(PostsController.class.getName());

    @Operation(summary = "Get all posts", description = "Fetch all posts from the database")
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        logger.info("User ID " + user.getId() + " is fetching all posts");
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Get paginated posts", description = "Fetch paginated list of posts from the database")
    @GetMapping("/paginated")
    public ResponseEntity<List<Post>> getPaginatedPosts(
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of posts per page", example = "10") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        logger.info("User ID " + user.getId() + " is fetching " + size + " posts on page " + page);
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAll(pageable);
        return ResponseEntity.ok(postPage.getContent());
    }

    @Operation(summary = "Get recommended posts", description = "Fetch paginated list of recommended posts based on likes and comments")
    @GetMapping("/recommendations")
    public ResponseEntity<List<Post>> getRecommendations(
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of posts per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        logger.info("Guest is fetching " + size + " posts on page " + page);
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByLikesDescCommentsDesc(pageable);
        return ResponseEntity.ok(postPage.getContent());
    }

    @Operation(summary = "Get post by ID", description = "Fetch a post by its ID from the database")
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(
            @Parameter(description = "ID of the post to be fetched", example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        logger.info("User ID " + user.getId() + " is fetching post with ID: " + id);
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            logger.warning("Post not found with ID: " + id + " for User ID: " + user.getId());
            throw new NotFoundException("Post not found");
        }
    }

    @Operation(summary = "Create a new post", description = "Create a new post with description, ingredients, and an optional image")
    @PostMapping
    public ResponseEntity<Post> createPost(
            @Parameter(description = "Description of the post", example = "This is a new post") @RequestParam("description") String description,
            @Parameter(description = "Ingredients of the post", example = "[\"Ingredient1\", \"Ingredient2\"]", required = false) @RequestParam(value = "ingredients", required = false) String[] ingredients,
            @Parameter(description = "Image for the post", required = false) @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        if (ingredients != null) {
            post.setIngredients(Arrays.asList(ingredients));
        }

        if (image != null && !image.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path path = Paths.get(uploadPath).resolve(filename);
                Files.write(path, image.getBytes());
                post.setImage("/uploads/" + filename);
            } catch (IOException e) {
                logger.severe("Could not upload image: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            post.setImage("placeholder.jpg");
        }

        Post createdPost = postRepository.save(post);
        logger.info("Post created with id: " + createdPost.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @Operation(summary = "Update a post", description = "Update a post with new description, ingredients, and an optional image")
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @Parameter(description = "ID of the post to be updated", example = "1") @PathVariable Long id,
            @Parameter(description = "New description for the post", example = "Updated description") @RequestParam("description") String description,
            @Parameter(description = "Updated ingredients for the post", example = "[\"Updated Ingredient1\", \"Updated Ingredient2\"]", required = false) @RequestParam(value = "ingredients", required = false) String[] ingredients,
            @Parameter(description = "Updated image for the post", required = false) @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Optional<Post> existingPost = postRepository.findById(id);

        if (!existingPost.isPresent()) {
            logger.warning("Post not found with ID: " + id + " for User ID: " + user.getId() + ". Update failed");
            throw new NotFoundException("Post not found");
        }

        Post existingPostEntity = existingPost.get();
        if (!existingPostEntity.getUser().getId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            logger.warning("Access denied for User ID: " + user.getId() + " when updating post with ID: " + id);
            throw new ForbiddenException("Access denied");
        }

        existingPostEntity.setDescription(description != null ? description : existingPostEntity.getDescription());
        existingPostEntity.setEdited(true);
        existingPostEntity.setUpdatedAt(LocalDateTime.now());

        if (ingredients != null) {
            try {
                existingPostEntity.setIngredients(new ArrayList<>(Arrays.asList(ingredients)));
            } catch (UnsupportedOperationException e) {
                logger.severe("Unsupported operation when setting ingredients: " + e.getMessage());
                throw e;
            }
        }

        if (image != null && !image.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path path = Paths.get(uploadPath).resolve(filename);
                Files.write(path, image.getBytes());
                existingPostEntity.setImage("/uploads/" + filename);
            } catch (IOException e) {
                logger.severe("Could not upload image: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        Post updatedPost = postRepository.save(existingPostEntity);
        logger.info("User ID " + user.getId() + " updated post with ID: " + updatedPost.getId());
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "Delete a post", description = "Delete already existed post")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID of the post to be deleted", example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Optional<Post> existingPost = postRepository.findById(id);

        if (!existingPost.isPresent()) {
            logger.warning("Post not found with ID: " + id + " for User ID: " + user.getId() + ". Delete failed");
            throw new NotFoundException("Post not found");
        }

        Post existingPostEntity = existingPost.get();
        if (!existingPostEntity.getUser().getId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            logger.warning("Access denied for User ID: " + user.getId() + " when deleting post with ID: " + id);
            throw new ForbiddenException("Access denied");
        }

        postRepository.deleteById(id);
        logger.info("User ID " + user.getId() + " deleted post with ID: " + id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Like a post", description = "Like or unlike a post by current user and update like counter")
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeOrUnlikePost(
            @Parameter(description = "ID of the post to be liked or unliked", example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(id, user.getId());

        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));

        if (existingLike.isPresent()) {
            // Если лайк уже существует, убираем его
            postLikeRepository.delete(existingLike.get());
            post.setLikesCount(post.getLikesCount() - 1);
            logger.info("User ID " + user.getId() + " removed like from post with ID: " + id);
            postRepository.save(post);
            return ResponseEntity.noContent().build();
        } else {
            // Если лайка нет, добавляем его
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user); // Set the entire user object
            post.setLikesCount(post.getLikesCount() + 1);
            postLikeRepository.save(like);
            logger.info("User ID " + user.getId() + " added like to post with ID: " + id);
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @Operation(summary = "Get current user's posts", description = "Fetch posts of logged user from the database")
    @GetMapping("/our_posts")
    public ResponseEntity<List<Post>> getCurrentUserPosts(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        logger.info("User ID " + user.getId() + " is fetching his posts");
        List<Post> posts = postRepository.findByUserId(user.getId());
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Add a comment to a post", description = "Add a new comment to a specific post")
    @PostMapping("/{id}/comment")
    public ResponseEntity<PostComment> addComment(
            @Parameter(description = "ID of the post to be commented", example = "1") @PathVariable Long id,
            @Parameter(description = "The comment to be added to the post", required = true) @RequestBody PostComment comment,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));

        comment.setUser(user); // Set the entire user object
        comment.setPost(post);

        PostComment createdComment = postCommentRepository.save(comment);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        logger.info("User ID " + user.getId() + " added comment to post with ID: " + id);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @Operation(summary = "Delete a comment from a post", description = "Delete a specific comment from a post by its ID")
    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the post from which the comment will be deleted", example = "1") @PathVariable Long postId,
            @Parameter(description = "ID of the comment to be deleted", example = "1") @PathVariable Long commentId,
            HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        PostComment existingComment = postCommentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));

        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingComment.getUser().getId().equals(user.getId()) && !existingComment.getPost().getUser().getId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            logger.warning("Access denied deleting comment" + commentId + " from post with id: " + postId + " for user id: " + user.getId());
            throw new ForbiddenException("Access denied");
        }

        postCommentRepository.delete(existingComment);
        post.setCommentsCount(post.getCommentsCount() - 1);
        postRepository.save(post);

        logger.info("Deleted comment with id: " + commentId + " from post with id: " + postId + " by user id:" + user.getId());
        return ResponseEntity.noContent().build();
    }

}

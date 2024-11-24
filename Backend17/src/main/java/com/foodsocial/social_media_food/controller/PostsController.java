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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/posts")
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

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        logger.info("User ID " + user.getId() + " is fetching all posts");
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id, HttpServletRequest request) {
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

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestParam("description") String description,
                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                           HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Post post = new Post();
        post.setDescription(description);
        post.setUserId(user.getId());

        if (image != null && !image.isEmpty()) {
            try {
                // Загрузим изображение
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path path = Paths.get(uploadPath).resolve(filename);
                Files.write(path, image.getBytes()); post.setImage("/uploads/" + filename);
            } catch (IOException e) {
                logger.severe("Could not upload image: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            post.setImage("placeholder.jpg"); // Заглушка
        }

        Post createdPost = postRepository.save(post);
        logger.info("Post created with id: " + createdPost.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Optional<Post> existingPost = postRepository.findById(id);

        if (!existingPost.isPresent()) {
            logger.warning("Post not found with ID: " + id + " for User ID: " + user.getId() + ". Update failed");
            throw new NotFoundException("Post not found");
        }

        Post existingPostEntity = existingPost.get();
        if (!existingPostEntity.getUserId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            logger.warning("Access denied for User ID: " + user.getId() + " when updating post with ID: " + id);
            throw new ForbiddenException("Access denied");
        }

        existingPostEntity.setDescription(post.getDescription() != null ? post.getDescription() : existingPostEntity.getDescription());
        existingPostEntity.setImage(post.getImage() != null ? post.getImage() : existingPostEntity.getImage());
        existingPostEntity.setUserId(existingPostEntity.getUserId());

        Post updatedPost = postRepository.save(existingPostEntity);
        logger.info("User ID " + user.getId() + " updated post with ID: " + updatedPost.getId());
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Optional<Post> existingPost = postRepository.findById(id);

        if (!existingPost.isPresent()) {
            logger.warning("Post not found with ID: " + id + " for User ID: " + user.getId() + ". Delete failed");
            throw new NotFoundException("Post not found");
        }

        Post existingPostEntity = existingPost.get();
        if (!existingPostEntity.getUserId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
            logger.warning("Access denied for User ID: " + user.getId() + " when deleting post with ID: " + id);
            throw new ForbiddenException("Access denied");
        }

        postRepository.deleteById(id);
        logger.info("User ID " + user.getId() + " deleted post with ID: " + id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeOrUnlikePost(@PathVariable Long id, HttpServletRequest request) {
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
            like.setUserId(user.getId());
            post.setLikesCount(post.getLikesCount() + 1);
            postLikeRepository.save(like);
            logger.info("User ID " + user.getId() + " added like to post with ID: " + id);
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping("/our_posts")
    public ResponseEntity<List<Post>> getCurrentUserPosts(HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        logger.info("User ID " + user.getId() + " is fetching his posts");
        List<Post> posts = postRepository.findByUserId(user.getId());
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<PostComment> addComment(@PathVariable Long id, @RequestBody PostComment comment, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));

        comment.setUserId(user.getId());
        comment.setPost(post);

        PostComment createdComment = postCommentRepository.save(comment);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        logger.info("User ID " + user.getId() + " added comment to post with ID: " + id);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpServletRequest request) {
        User user = userService.getAuthenticatedUser(request);
        PostComment existingComment = postCommentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));

        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingComment.getUserId().equals(user.getId()) && !existingComment.getPost().getUserId().equals(user.getId()) && !user.getRoles().contains("ADMIN")) {
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

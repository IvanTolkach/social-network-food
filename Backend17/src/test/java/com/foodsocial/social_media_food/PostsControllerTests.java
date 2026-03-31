package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.controller.PostsController;
import com.foodsocial.social_media_food.domain.Post;
import com.foodsocial.social_media_food.domain.PostComment;
import com.foodsocial.social_media_food.domain.PostLike;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.PostCommentRepository;
import com.foodsocial.social_media_food.repos.PostRepository;
import com.foodsocial.social_media_food.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostsControllerTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCommentRepository postCommentRepository;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PostsController postsController;

    private final String uploadPath = "src/main/resources/static/uploads";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(postsController, "uploadPath", uploadPath);
    }

    @Test
    void testGetRecommendations() {
        Post mockPost1 = new Post();
        mockPost1.setId(1L);
        mockPost1.setDescription("Post 1");
        mockPost1.setLikes(Arrays.asList(new PostLike(), new PostLike()));
        mockPost1.setComments(Arrays.asList(new PostComment(), new PostComment(), new PostComment()));

        Post mockPost2 = new Post();
        mockPost2.setId(2L);
        mockPost2.setDescription("Post 2");
        mockPost2.setLikes(Arrays.asList(new PostLike()));
        mockPost2.setComments(Arrays.asList(new PostComment()));

        List<Post> posts = Arrays.asList(mockPost1, mockPost2);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(postRepository.findAllByOrderByLikesDescCommentsDesc(any(Pageable.class))).thenReturn(postPage);

        ResponseEntity<List<Post>> response = postsController.getRecommendations(0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Post 1", response.getBody().get(0).getDescription());
    }

    @Test
    void testGetPaginatedPosts() {
        Post mockPost1 = new Post();
        mockPost1.setId(1L);
        mockPost1.setDescription("Post 1");

        Post mockPost2 = new Post();
        mockPost2.setId(2L);
        mockPost2.setDescription("Post 2");

        List<Post> posts = Arrays.asList(mockPost1, mockPost2);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

        ResponseEntity<List<Post>> response = postsController.getPaginatedPosts(0, 10, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetPostById() {
        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);

        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setDescription("Test Description");
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        ResponseEntity<Post> response = postsController.getPostById(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Description", response.getBody().getDescription());
    }

    @Test
    void testCreatePost() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);

        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setDescription("Test Description");
        mockPost.setUser(mockUser);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image content".getBytes());

        ResponseEntity<Post> response = postsController.createPost("Test Description", new String[]{"Ingredient1", "Ingredient2"}, image, request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Test Description", response.getBody().getDescription());
    }

    @Test
    void testUpdatePost() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setUser(mockUser);
        existingPost.setDescription("Old Description");
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        Post updatedPost = new Post();
        updatedPost.setId(1L);
        updatedPost.setDescription("Updated Description");
        updatedPost.setUser(mockUser);
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image content".getBytes());

        ResponseEntity<Post> response = postsController.updatePost(1L, "Updated Description", new String[]{"Updated Ingredient1", "Updated Ingredient2"}, image, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Description", response.getBody().getDescription());
    }

    @Test
    void testDeletePost() {
        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setUser(mockUser);  // Устанавливаем User
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        doNothing().when(postRepository).deleteById(1L);

        ResponseEntity<Void> response = postsController.deletePost(1L, request);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testAddComment() {
        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);

        Post mockPost = new Post();
        mockPost.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        PostComment mockComment = new PostComment();
        mockComment.setId(1L);
        mockComment.setDescription("Test comment");
        mockComment.setUser(mockUser);
        when(postCommentRepository.save(any(PostComment.class))).thenReturn(mockComment);

        PostComment comment = new PostComment();
        comment.setDescription("Test comment");

        ResponseEntity<PostComment> response = postsController.addComment(1L, comment, request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Test comment", response.getBody().getDescription());
    }

    @Test
    void testDeleteComment() {
        User mockUser = new User();
        mockUser.setId(1);
        when(userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(mockUser);

        PostComment existingComment = new PostComment();
        existingComment.setId(1L);
        existingComment.setUser(mockUser);
        when(postCommentRepository.findById(1L)).thenReturn(Optional.of(existingComment));

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setUser(mockUser);
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        doNothing().when(postCommentRepository).delete(existingComment);

        ResponseEntity<Void> response = postsController.deleteComment(1L, 1L, request);

        assertEquals(204, response.getStatusCodeValue());
    }
}

package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.controller.UserController;
import com.foodsocial.social_media_food.domain.Role;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.security.NotFoundException;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import com.foodsocial.social_media_food.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testUpdateUser() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setAvatar("/static/uploads/DefaultAvatar.jpg");
        mockUser.setRoles(Collections.singleton(Role.USER));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getAuthenticatedUser(request)).thenReturn(mockUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        MockMultipartFile avatar = new MockMultipartFile("avatar", "newAvatar.jpg", "image/jpeg", "test image content".getBytes());

        long fixedTime = 1733658081615L;
        String expectedFilename = fixedTime + "_newAvatar.jpg";

        ResponseEntity<Object> response = userController.updateUser(1, "newusername", avatar, request, fixedTime);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newusername", ((User) response.getBody()).getUsername());
        assertEquals("/static/uploads/" + expectedFilename, ((User) response.getBody()).getAvatar());
    }

    @Test
    void testUpdateUserUnauthorized() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setAvatar("/static/uploads/DefaultAvatar.jpg");
        mockUser.setRoles(Collections.singleton(Role.USER));

        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setAvatar("/static/uploads/DefaultAvatar.jpg");
        otherUser.setRoles(Collections.singleton(Role.USER));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getAuthenticatedUser(request)).thenReturn(otherUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        assertThrows(UnauthorizedException.class, () -> {
            userController.updateUser(1, "newusername", null, request, null);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setAvatar("/static/uploads/DefaultAvatar.jpg");
        mockUser.setRoles(Collections.singleton(Role.USER));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getAuthenticatedUser(request)).thenReturn(mockUser);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userController.updateUser(1, "newusername", null, request, null);
        });
    }

    @Test
    void testUpdateUserDuplicateUsername() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setAvatar("/static/uploads/DefaultAvatar.jpg");
        mockUser.setRoles(Collections.singleton(Role.USER));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getAuthenticatedUser(request)).thenReturn(mockUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Duplicate username"));

        MockMultipartFile avatar = new MockMultipartFile("avatar", "newAvatar.jpg", "image/jpeg", "test image content".getBytes());

        long fixedTime = 1733658081615L;

        ResponseEntity<Object> response = userController.updateUser(1, "duplicateUsername", avatar, request, fixedTime);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User with this name already exists.", response.getBody());
    }

    @Test
    void testGetAllUsersAuthorized() {
        User mockUser1 = new User();
        mockUser1.setId(1);
        mockUser1.setUsername("User1");

        User mockUser2 = new User();
        mockUser2.setId(2);
        mockUser2.setUsername("User2");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findAll()).thenReturn(Arrays.asList(mockUser1, mockUser2));

        ResponseEntity<Iterable<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, ((Iterable<User>)response.getBody()).spliterator().getExactSizeIfKnown());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsersUnauthorized() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            userController.getAllUsers();
        });

        verify(userRepository, never()).findAll();
    }
}

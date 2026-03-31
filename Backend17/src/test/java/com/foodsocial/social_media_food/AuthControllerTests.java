package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.controller.AuthController;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.requests.LoginRequest;
import com.foodsocial.social_media_food.requests.SignupRequest;
import com.foodsocial.social_media_food.service.CookieService;
import com.foodsocial.social_media_food.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private CookieService cookieService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("TestUser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("TestUser");

        when(userService.registerUser(anyString(), anyString(), anyString(),anyString())).thenReturn(mockUser);

        ResponseEntity<String> response = authController.registerUser(signupRequest, this.response);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void testLoginUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("TestUser");
        loginRequest.setPassword("password");

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("TestUser");

        when(userService.loginUser(anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<String> response = authController.loginUser(loginRequest, request, this.response);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    void testLogoutUser() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("TestUser");

        // Сначала выполняем login для установки cookie
        Cookie mockCookie = new Cookie("token", "sample-token");
        when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
        when(cookieService.ifAuthUser(anyString())).thenReturn(mockUser);

        // Устанавливаем аутентификацию пользователя
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Выполняем логаут
        ResponseEntity<String> response = authController.logoutUser(request, this.response);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful for user: TestUser", response.getBody());

        // Очистка контекста безопасности
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCurrentUser() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("TestUser");

        Cookie mockCookie = new Cookie("token", "sample-token");

        when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
        when(cookieService.ifAuthUser(anyString())).thenReturn(mockUser);

        ResponseEntity<User> response = authController.currentUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("TestUser", response.getBody().getUsername());
    }
}

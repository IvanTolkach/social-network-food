package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.requests.LoginRequest;
import com.foodsocial.social_media_food.requests.SignupRequest;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import com.foodsocial.social_media_food.service.CookieService;
import com.foodsocial.social_media_food.service.UserService;
import com.foodsocial.social_media_food.validation.PasswordGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@Tag(name = "Authentication", description = "API for user authentication")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Operation(summary = "Register a new user", description = "Register a new user with username, email, and password")
    @PostMapping("/register")
    public @ResponseBody ResponseEntity<String> registerUser(
            @Parameter(description = "Signup request containing username, email, and password", required = true) @RequestBody SignupRequest signUpRequest,
            HttpServletResponse response) {
        try {
            // Register user
            User user = userService.registerUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword(),
                    "/static/uploads/DefaultAvatar.jpg"
            );

            // Generate unique token
            String token = UUID.randomUUID().toString();
            userService.addCookie(user, token);

            // Add cookie
            cookieService.addAuthCookie(response, token);

            logger.info("User registered successfully with username: " + user.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            logger.warning("Error during user registration: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Login a user", description = "Authenticate a user with identifier (username or email) and password")
    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> loginUser(
            @Parameter(description = "Login request containing identifier and password", required = true) @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            User user = userService.loginUser(loginRequest.getIdentifier(), loginRequest.getPassword());

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    loginRequest.getPassword(),
                    List.of(new SimpleGrantedAuthority("USER"))
            );

            // Generate unique token
            String token = UUID.randomUUID().toString();
            userService.addCookie(user, token);

            // Add cookie
            cookieService.addAuthCookie(response, token);

            logger.info("Login successful for user: " + user.getUsername());
            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException e) {
            logger.warning("Login failed for identifier: " + loginRequest.getIdentifier() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(summary = "Handle login via Google", description = "Handle login via Google")
    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/google";
    }

    @Operation(summary = "Handle successful login via Google", description = "Handle successful login via Google and set authentication cookie")
    @GetMapping("/loginSuccess")
    public String loginSuccess(HttpServletResponse response) {
        // Получаем текущего пользователя из SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Логируем всю информацию об аутентификации для отладки
        logger.info("Authentication object: " + authentication);

        if (authentication != null) {
            logger.info("Principal class: " + authentication.getPrincipal().getClass());

            if (authentication.getPrincipal() instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

                logger.info("OidcUser: " + oidcUser);

                // Получаем email и имя из Google профиля
                String email = oidcUser.getEmail();
                String fullName = oidcUser.getFullName();
                String pictureUrl = oidcUser.getPicture();

                logger.info("Google user details - Email: " + email + ", Name: " + fullName);

                // Найти пользователя в базе данных
                User user = userService.findUserByEmail(email);

                String generatedPassword = PasswordGenerator.generatePassword(12); // Генерируем пароль длиной 12 символов
                String hashedPassword = passwordEncoder.encode(generatedPassword);

                if (user == null) {
                    // Регистрация нового пользователя
                    user = userService.registerUser(
                            fullName,        // Имя пользователя
                            email,           // Email
                            hashedPassword,              // Пустой пароль, так как Google аутентификация
                            pictureUrl       // Аватар из Google
                    );
                }

                // Сгенерировать уникальный токен и добавить в БД
                String token = UUID.randomUUID().toString();
                userService.addCookie(user, token);

                // Установить токен в куки
                cookieService.addAuthCookie(response, token);

                logger.info("Login successful for user: " + user.getUsername());
                return "redirect:http://localhost:3000"; // Главная страница
            } else {
                logger.warning("Principal is not an instance of OidcUser. Found: " + authentication.getPrincipal().getClass());
            }
        } else {
            logger.warning("Authentication object is null.");
        }

        throw new IllegalStateException("Google authentication failed, no OidcUser found");
    }

    @Operation(summary = "Handle error while login via Google")
    @GetMapping("/error")
    public String errorLogin(HttpServletResponse response) {
        throw new IllegalStateException("Error while login via Google");
    }

    @Operation(summary = "Logout the current user", description = "Logout the currently authenticated user and clear the authentication cookie")
    @GetMapping("/logout")
    public @ResponseBody ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            SecurityContextHolder.clearContext();

            // Get token from cookie
            Cookie tokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);

            if (tokenCookie != null) {
                try {
                    User user = cookieService.ifAuthUser(tokenCookie.getValue());
                    userService.deleteCookie(tokenCookie.getValue());

                    // Delete cookie
                    cookieService.deleteAuthCookie(response);

                    logger.info("Logout successful for user: " + user.getUsername());
                    return ResponseEntity.ok("Logout successful for user: " + user.getUsername());
                } catch (Exception e) {
                    logger.warning("Logout failed: " + e.getMessage());
                    throw new UnauthorizedException(e.getMessage());
                }
            } else {
                logger.warning("No valid token found during logout");
                throw new UnauthorizedException("No valid token found");
            }
        } else {
            logger.warning("No user is currently logged in during logout attempt");
            throw new UnauthorizedException("No user is currently logged in");
        }
    }

    @Operation(summary = "Get current user", description = "Fetch the currently authenticated user based on the authentication cookie")
    @GetMapping("/current")
    public @ResponseBody ResponseEntity<User> currentUser(HttpServletRequest request) {
        logger.info("Checking current user");

        // Get token from cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            logger.warning("No cookies found");
            throw new UnauthorizedException("No cookies found");
        }

        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .orElse(null);

        if (tokenCookie != null) {
            try {
                User user = cookieService.ifAuthUser(tokenCookie.getValue());
                logger.info("Current user: " + user.getUsername() + " with user id: " + user.getId());
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                logger.warning("Failed to fetch current user: " + e.getMessage());
                throw new UnauthorizedException(e.getMessage());
            }
        } else {
            logger.warning("No valid token found for current user");
            throw new UnauthorizedException("No valid token found");
        }
    }

    @Operation(summary = "Get current user after Google authentication", description = "Fetch the currently authenticated user based on the authentication cookie after Google authentication")
    @GetMapping("/currentGoogle")
    public @ResponseBody ResponseEntity<User> currentUserGoogle(HttpServletRequest request) {
        logger.info("Checking current user");

        // Get token from cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            logger.warning("No cookies found");
            return ResponseEntity.status(209).body(null);
        }

        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .orElse(null);

        if (tokenCookie != null) {
            try {
                User user = cookieService.ifAuthUser(tokenCookie.getValue());
                logger.info("Current user: " + user.getUsername() + " with user id: " + user.getId());
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                logger.warning("Failed to fetch current user: " + e.getMessage());
                return ResponseEntity.status(404).body(null);
            }
        } else {
            logger.warning("No valid token found for current user");
            return ResponseEntity.status(209).body(null);
        }
    }
}

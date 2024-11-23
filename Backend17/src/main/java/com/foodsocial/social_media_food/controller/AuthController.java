package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.requests.LoginRequest;
import com.foodsocial.social_media_food.requests.SignupRequest;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import com.foodsocial.social_media_food.service.CookieService;
import com.foodsocial.social_media_food.service.UserService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CookieService cookieService;

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/register")
    public @ResponseBody ResponseEntity<String> registerUser(@RequestBody SignupRequest signUpRequest, HttpServletResponse response) {
        try {
            // Регистрация пользователя
            User user = userService.registerUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword()
            );

            // Генерация уникального токена
            String token = UUID.randomUUID().toString();
            userService.addCookie(user, token);

            // Добавление куки
            cookieService.addAuthCookie(response, token);

            logger.info("User registered successfully with username: " + user.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            logger.warning("Error during user registration: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = userService.loginUser(loginRequest.getIdentifier(), loginRequest.getPassword());

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    loginRequest.getPassword(),
                    List.of(new SimpleGrantedAuthority("USER"))
            );

            // Генерация уникального токена
            String token = UUID.randomUUID().toString();
            userService.addCookie(user, token);

            //Добавление куки
            cookieService.addAuthCookie(response, token);

            logger.info("Login successful for user: " + user.getUsername());
            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException e) {
            logger.warning("Login failed for identifier: " + loginRequest.getIdentifier() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public @ResponseBody ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            SecurityContextHolder.clearContext();

            // Проверка наличия куки
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        try {
                            User user = cookieService.ifAuthUser(cookie.getValue());
                            userService.deleteCookie(cookie.getValue());

                            // Удаление куки
                            cookieService.deleteAuthCookie(response);

                            logger.info("Logout successful for user: " + user.getUsername());
                            return ResponseEntity.ok("Logout successful for user: " + user.getUsername());
                        } catch (Exception e) {
                            logger.warning("Logout failed: " + e.getMessage());
                            throw new UnauthorizedException(e.getMessage());
                        }
                    }
                }
            }

            logger.warning("No valid token found during logout");
            throw new UnauthorizedException("No valid token found");
        } else {
            logger.warning("No user is currently logged in during logout attempt");
            throw new UnauthorizedException("No user is currently logged in");
        }
    }

    @GetMapping("/current")
    public @ResponseBody ResponseEntity<String> currentUser(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Checking current user");

        // Получение куки, а именно списка, тебе нужен по getName тот, который ты задал
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).forEach(cookie -> logger.info("Cookie: " + cookie.getName() + " " + cookie.getValue()));
        }
        return ResponseEntity.ok("{\n\t'status': 'OK'\n\t'data': 'success'\n}");
    }
}

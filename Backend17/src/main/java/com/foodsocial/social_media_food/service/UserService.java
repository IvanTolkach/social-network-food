package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User registerUser(String username, String email, String password, String avatar);

    User loginUser(String identifier, String password);

    @Async
    void addCookie(User user, String token);

    void deleteCookie(String token);

    User getAuthenticatedUser(HttpServletRequest request);

    User findUserByEmail(String email); // Новый метод

    void saveUser(User user); // Новый метод
}

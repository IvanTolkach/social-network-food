package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.accessingdatasql.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User registerUser(String username, String email, String password);

    User loginUser(String identifier, String password);

    void addCookie(int userId, String token);

    void deleteCookie(String token);
}

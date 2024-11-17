package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // This means that this class is a Controller
public class MainController {

    @Autowired // This means to get the bean called userRepository
    private UserRepository userRepository;

    @GetMapping("/all")
    public @ResponseBody ResponseEntity<Iterable<User>> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Iterable<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } else {
            throw new UnauthorizedException("Unauthorized request");
        }
    }
}
package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller // This means that this class is a Controller
public class MainController {

    @Autowired // This means to get the bean called userRepository
    private UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @GetMapping("/all")
    public @ResponseBody ResponseEntity<Iterable<User>> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Iterable<User> users = userRepository.findAll();
            logger.info("Fetched all users successfully");
            return ResponseEntity.ok(users);
        } else {
            logger.warning("Unauthorized request to fetch all users");
            throw new UnauthorizedException("Unauthorized request");
        }
    }
}

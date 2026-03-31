package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.domain.Role;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.security.NotFoundException;
import com.foodsocial.social_media_food.service.UserService;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "API for displaying and editing user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Operation(summary = "Get all users", description = "Fetch all users from the database")
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

    @Operation(summary = "Get user by ID", description = "Fetch a user by their ID from the database")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @Parameter(description = "ID of the user to be fetched", example = "1") @PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("Fetched user with ID: " + id);
            return ResponseEntity.ok(user.get());
        } else {
            logger.warning("User not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update user", description = "Update user details including username and avatar")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @Parameter(description = "ID of the user to be updated", example = "1") @PathVariable Integer id,
            @Parameter(description = "New username for the user", example = "ExampleUsername") @RequestParam(required = false) String username,
            @Parameter(description = "New avatar for the user", required = false) @RequestParam(required = false) MultipartFile avatar,
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(description = "Current timestamp for avatar naming", example = "1733658081615", required = false) @RequestParam(required = false) Long currentTimeMillis) {
        if (currentTimeMillis == null) {
            currentTimeMillis = System.currentTimeMillis();
        }

        User authenticatedUser = userService.getAuthenticatedUser(request);

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (authenticatedUser.getRoles().contains(Role.ADMIN) || user.getId().equals(authenticatedUser.getId())) {
                if (username != null && !username.equals(user.getUsername())) {
                    user.setUsername(username);
                }
                if (avatar != null && !avatar.isEmpty()) {
                    try {
                        String filename = currentTimeMillis + "_" + avatar.getOriginalFilename();
                        Path path = Paths.get("src/main/resources/static/uploads").resolve(filename);
                        Files.write(path, avatar.getBytes());
                        user.setAvatar("/static/uploads/" + filename);
                        logger.info("Saved avatar for user with ID: " + id);
                    } catch (IOException e) {
                        logger.severe("Error saving avatar for user with ID: " + id + ". Error: " + e.getMessage());
                        return ResponseEntity.status(500).body("Error saving avatar.");
                    }
                }
                try {
                    userRepository.save(user);
                    logger.info("Updated user with ID: " + id);
                } catch (DataIntegrityViolationException e) {
                    logger.warning("Attempt to update user with duplicate username: " + username);
                    return ResponseEntity.status(400).body("User with this name already exists.");
                }
                return ResponseEntity.ok(user);
            } else {
                logger.warning("Unauthorized attempt to edit user with ID: " + id);
                throw new UnauthorizedException("You have no permission to edit this profile.");
            }
        } else {
            logger.warning("User not found with ID: " + id);
            throw new NotFoundException("User doesn't exists.");
        }
    }
}

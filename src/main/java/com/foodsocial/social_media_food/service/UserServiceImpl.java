package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.accessingdatasql.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PasswordService passwordService;

    // Метод для регистрации пользователя
    @Override
    public User registerUser(String username, String email, String password) {

        if (!validationService.validateEmail(email)) {
            throw new RuntimeException("Invalid email format");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already in use");
        }

        if (!validationService.validateUsername(username)) {
            throw new RuntimeException("Invalid username format. Username may only " +
                    "contain alphanumeric characters or single hyphens, and cannot begin or end with a hyphen.");
        }

        if (!validationService.validatePassword(password, username)) {
            throw new RuntimeException("Password is too weak.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordService.hashPassword(password));

        // Сохранение пользователя в базе данных
        return userRepository.save(user);
    }

    // TODO
    @Override
    public User loginUser(String username, String email, String password) {
        return null;
    }
}

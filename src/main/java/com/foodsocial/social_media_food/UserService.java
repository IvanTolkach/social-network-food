package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.accessingdatasql.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Аннотация @Service указывает, что класс будет управляться Spring как сервисный компонент
@Service
public class UserService {

    // Внедрение репозитория для доступа к данным пользователя
    @Autowired
    private UserRepository userRepository;

    // Внедрение PasswordEncoder для хеширования паролей
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Метод для регистрации пользователя
    public User registerUser(String email, String username, String password) {
        // Проверка, что email уникален
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Проверка, что username уникален
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already in use");
        }

        // Создание нового пользователя и хеширование его пароля
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        // Сохранение пользователя в базе данных
        return userRepository.save(user);
    }
}

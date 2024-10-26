package com.foodsocial.social_media_food;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Аннотация @RestController указывает, что класс является контроллером REST API
@RestController
@RequestMapping("/api") // Базовый путь для всех методов в этом контроллере
public class UserController {

    // Внедрение UserService для работы с пользователями
    @Autowired
    private UserService userService;

    // Обработка POST-запроса для регистрации пользователя
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        try {
            // Регистрация пользователя
            userService.registerUser(request.getEmail(), request.getUsername(), request.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            // Возврат ошибки, если регистрация не удалась
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// Класс запроса для регистрации, содержащий email, username и password
class UserRegistrationRequest {
    private String email;
    private String username;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

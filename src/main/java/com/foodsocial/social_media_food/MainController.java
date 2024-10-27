package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.service.UserService;
import com.foodsocial.social_media_food.accessingdatasql.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo")
public class MainController {
    @Autowired // This means to get the bean called userRepository
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping(path="/register")
    public @ResponseBody ResponseEntity<String> registerUser(@RequestParam String username,
                                                             @RequestParam String email,
                                                             @RequestParam String password  ) {

        try {
            // Регистрация пользователя
            userService.registerUser(username, email, password);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            // Возврат ошибки, если регистрация не удалась
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/login")
    public @ResponseBody ResponseEntity<String> loginUser(@RequestParam String identifier,
                                                          @RequestParam String password     ) {

        try {
            User user = userService.loginUser(identifier, password);
            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}
package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.service.UserService;
import com.foodsocial.social_media_food.service.ValidationServiceImpl;
import com.foodsocial.social_media_food.accessingdatasql.User;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

        ValidationService validationService = new ValidationServiceImpl();

        boolean isEmail = validationService.validateEmail(identifier);
        Optional<User> userOpt = isEmail
                ? userRepository.findByEmail(identifier)
                : userRepository.findByUsername(identifier);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Сравниваем захешированный пароль
            if (passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}
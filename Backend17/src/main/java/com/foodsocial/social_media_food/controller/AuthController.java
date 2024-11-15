package com.foodsocial.social_media_food.controller;

import com.foodsocial.social_media_food.accessingdatasql.User;
import com.foodsocial.social_media_food.configuration.CookieConfig;
import com.foodsocial.social_media_food.requests.LoginRequest;
import com.foodsocial.social_media_food.requests.SignupRequest;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import com.foodsocial.social_media_food.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/demo")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public @ResponseBody ResponseEntity<String> registerUser(@RequestBody SignupRequest signUpRequest, HttpServletResponse response) {
        try {
            System.out.println("user");
            // Регистрация пользователя
            User registeredUser = userService.registerUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword()
            );

            // Генерация уникального токена
            String token = UUID.randomUUID().toString();
            userService.addCookie(registeredUser.getId(), token);

            // Добавление куки
            CookieConfig.addAuthCookie(response, token);

            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            // Обработка ошибок регистрации
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = userService.loginUser(loginRequest.getIdentifier(), loginRequest.getPassword());

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    loginRequest.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Генерация уникального токена
            String token = UUID.randomUUID().toString();
            userService.addCookie(user.getId(), token);

            //Добавление куки
            CookieConfig.addAuthCookie(response, token);

            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public @ResponseBody ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            SecurityContextHolder.clearContext();

            // Проверка наличия куки
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        userService.deleteCookie(cookie.getValue());
                        break;
                    }
                }
            }

            // Удаление куки
            CookieConfig.deleteAuthCookie(response);

            return ResponseEntity.ok("Logout successful");
        } else {
            throw new UnauthorizedException("No user is currently logged in");
        }
    }

    @GetMapping("/current")
    public @ResponseBody ResponseEntity<String> currentUser(HttpServletRequest request, HttpServletResponse response) {

        //получение куки, а именно списка, тебе нужен по getName тот, который ты задал
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            Arrays.stream(cookies).forEach(cookie -> System.out.println(cookie.getName() + " " + " " + cookie.getValue()));
        }
        return ResponseEntity.ok("{\n\t'status': 'OK'\n\t'data': 'success'\n}");
    }

}

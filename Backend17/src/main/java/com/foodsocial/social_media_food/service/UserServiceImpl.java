package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.domain.Cookies;
import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.domain.Role;
import com.foodsocial.social_media_food.repos.CookieRepository;
import com.foodsocial.social_media_food.repos.UserRepository;
import com.foodsocial.social_media_food.security.AuthenticationException;
import com.foodsocial.social_media_food.security.UserAlreadyExistsException;
import com.foodsocial.social_media_food.security.ValidationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private CookieRepository cookieRepository;

    @Override
    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already in use");
        }
        if (!validationService.validateUsername(username)) {
            throw new ValidationException("Invalid username format. Username may only " +
                    "contain alphanumeric characters or single hyphens, and cannot begin or end with a hyphen.");
        }
        if (!validationService.validateEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }
        if (!validationService.validatePassword(password, username)) {
            throw new ValidationException("Password is too weak.");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordService.hashPassword(password));
        user.setRoles(Collections.singleton(Role.USER));

        return userRepository.save(user);
    }

    @Override
    public User loginUser(String identifier, String password) {
        boolean isEmail = validationService.validateEmail(identifier);
        Optional<User> userOpt = isEmail
                ? userRepository.findByEmail(identifier)
                : userRepository.findByUsername(identifier);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordService.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new AuthenticationException("Incorrect login or password");
            }
        } else {
            throw new AuthenticationException("Incorrect login or password");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Преобразование Set<Role> в List<GrantedAuthority>
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public void addCookie(User user, String token) {
        Cookies cookies = new Cookies();
        cookies.setUser(user);
        cookies.setToken(token);
        cookies.setExpiration(LocalDateTime.now().plusDays(30));
        cookieRepository.save(cookies);
    }

    @Transactional
    public void deleteCookie(String token) {
        cookieRepository.deleteByToken(token);
    }

    public User getUserByToken(String token) {
        Cookies cookies = cookieRepository.findByToken(token);
        if (cookies != null && !cookies.isExpired()) {
            return cookies.getUser();
        }
        return null;
    }
}

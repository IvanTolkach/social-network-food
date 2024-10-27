package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.validation.PasswordValidator;
import com.foodsocial.social_media_food.validation.UsernameValidator;
import com.foodsocial.social_media_food.validation.EmailValidator;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final PasswordValidator passwordValidator = new PasswordValidator();
    private final UsernameValidator usernameValidator = new UsernameValidator();
    private final EmailValidator emailValidator = new EmailValidator();

    @Override
    public boolean validatePassword(String password, String username) {
        return passwordValidator.isValid(password, username);
    }

    @Override
    public boolean validateUsername(String username) {
        return usernameValidator.isValid(username);
    }

    @Override
    public boolean validateEmail(String email) {
        return emailValidator.isValid(email);
    }
}
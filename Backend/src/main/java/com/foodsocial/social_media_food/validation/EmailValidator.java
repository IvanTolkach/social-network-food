package com.foodsocial.social_media_food.validation;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false; // Проверка на null или пустую строку
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
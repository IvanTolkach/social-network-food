package com.foodsocial.social_media_food.validation;

import java.util.regex.Pattern;

public class UsernameValidator {
    private static final String USERNAME_REGEX = "^(?!-)(?!.*\\s{2,})[A-Za-z0-9- ]+(?<!-)$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    public boolean isValid(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false; // Проверка на null или пустую строку с учетом пробелов
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
}

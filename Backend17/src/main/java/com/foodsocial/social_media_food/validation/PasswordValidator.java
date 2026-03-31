package com.foodsocial.social_media_food.validation;

public class PasswordValidator {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 64;

    public boolean isValid(String password) {
        return isLengthValid(password);
    }

    private boolean isLengthValid(String password) {
        return password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH;
    }
}



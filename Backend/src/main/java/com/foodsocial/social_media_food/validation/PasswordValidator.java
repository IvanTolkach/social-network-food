package com.foodsocial.social_media_food.validation;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;
    private static final Pattern COMMON_PASSWORDS_PATTERN = Pattern.compile("^(password|123456|qwerty|abc123|letmein)$", Pattern.CASE_INSENSITIVE);

    public boolean isValid(String password, String username) {
        return isLengthValid(password) &&
                !isCommonPassword(password) &&
                !containsSequentialCharacters(password) &&
                !containsContextualWords(password, username);
    }

    private boolean isLengthValid(String password) {
        return password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH;
    }

    private boolean isCommonPassword(String password) {
        return COMMON_PASSWORDS_PATTERN.matcher(password).matches(); // Можно расширить список
    }

    private boolean containsSequentialCharacters(String password) {
        return password.matches(".*(012|123|234|345|456|567|678|789|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz).*") ||
                password.matches(".*(.)\\1{2,}.*"); // Проверка на повторяющиеся символы
    }

    private boolean containsContextualWords(String password, String username) {
        return password.toLowerCase().contains(username.toLowerCase());
    }
}



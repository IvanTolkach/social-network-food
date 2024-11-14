package com.foodsocial.social_media_food.service;

public interface ValidationService {

    boolean validatePassword(String password, String username);

    boolean validateUsername(String username);

    boolean validateEmail(String email);
}

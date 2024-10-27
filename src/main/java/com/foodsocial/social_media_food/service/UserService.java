package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.accessingdatasql.User;

public interface UserService {

    User registerUser(String username, String email, String password);

    User loginUser(String identifier, String password);
}

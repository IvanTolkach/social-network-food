package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.accessingdatasql.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    // Метод для поиска пользователя по email
    Optional<User> findByEmail(String email);

    // Метод для поиска пользователя по username
    Optional<User> findByUsername(String username);
}

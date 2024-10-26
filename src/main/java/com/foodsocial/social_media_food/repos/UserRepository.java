package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.accessingdatasql.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}

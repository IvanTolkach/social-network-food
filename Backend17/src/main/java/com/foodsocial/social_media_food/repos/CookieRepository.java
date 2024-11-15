package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.accessingdatasql.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookieRepository extends JpaRepository<Cookie, Long> {
    void deleteByToken(String token);
}

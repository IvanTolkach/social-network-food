package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.domain.Cookies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookieRepository extends JpaRepository<Cookies, Long> {

    void deleteByToken(String token);

    Cookies findByToken(String token);
}

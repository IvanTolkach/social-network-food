package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Integer userId);
}

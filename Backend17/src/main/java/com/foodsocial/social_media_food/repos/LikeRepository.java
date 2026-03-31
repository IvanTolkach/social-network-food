package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findByPostId(Long postId);
}
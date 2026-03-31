package com.foodsocial.social_media_food.repos;

import com.foodsocial.social_media_food.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Integer userId);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByOrderByLikesDescCommentsDesc(Pageable pageable);
}

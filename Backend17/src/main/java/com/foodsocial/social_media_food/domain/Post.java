package com.foodsocial.social_media_food.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Schema(description = "Schema for Post entity")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the post", example = "1")
    private Long id;

    @Schema(description = "Image URL of the post", example = "/uploads/image.jpg")
    private String image = "placeholder.jpg"; // Placeholder for the image

    @Schema(description = "Description of the post", example = "This is a new post")
    private String description;

    @Schema(description = "Flag indicating if the post has been edited", example = "false")
    private boolean edited = false;

    @Schema(description = "Time when the post was created", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Time when the post was last updated", example = "2024-01-02T12:00:00")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of likes associated with the post")
    private List<PostLike> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("post-comments")
    @Schema(description = "List of comments associated with the post")
    private List<PostComment> comments;

    @ElementCollection
    @Schema(description = "List of ingredients associated with the post", example = "[\"Ingredient1\", \"Ingredient2\"]")
    private List<String> ingredients;

    @Schema(description = "Count of likes on the post", example = "10")
    private int likesCount;

    @Schema(description = "Count of comments on the post", example = "5")
    private int commentsCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User who created the post", example = "42")
    @JsonProperty("user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<PostLike> getLikes() {
        return likes;
    }

    public void setLikes(List<PostLike> likes) {
        this.likes = likes;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

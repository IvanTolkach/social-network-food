package com.foodsocial.social_media_food.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Schema(description = "Schema for Cookies entity")
public class Cookies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the cookie", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @Schema(description = "User associated with this cookie")
    private User user;

    @Column(nullable = false)
    @Schema(description = "Token value of the cookie", example = "b204df6b-91f1-42d5-b47c-612b9f183a5d")
    private String token;

    @Column(nullable = false)
    @Schema(description = "Expiration date and time of the cookie", example = "2023-12-31T23:59:59")
    private LocalDateTime expiration;

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }
}

package com.foodsocial.social_media_food.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Set;

@Entity
@Table(name = "app_user")
@Schema(description = "Schema for User entity")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier of the user", example = "1")
    private Integer id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Email
    @Column(nullable = false, unique = true)
    @Schema(description = "Email of the user", example = "john@example.com")
    private String email;

    @Min(6)
    @Max(64)
    @Column(nullable = false)
    @Schema(description = "Password of the user", example = "password123")
    private String password;

    @Column(nullable = false)
    @Schema(description = "Avatar URL of the user", example = "/static/uploads/DefaultAvatar.jpg")
    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Set of cookies associated with the user")
    private Set<Cookies> cookies;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Schema(description = "Roles assigned to the user")
    private Set<Role> roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Set<Cookies> getCookies() {
        return cookies;
    }

    public void setCookies(Set<Cookies> cookies) {
        this.cookies = cookies;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}

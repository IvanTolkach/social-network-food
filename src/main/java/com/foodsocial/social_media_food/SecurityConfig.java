package com.foodsocial.social_media_food;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable) // Отключение CSRF с использованием CsrfConfigurer
                .authorizeRequests(auth -> auth
                        .requestMatchers("/demo/register").permitAll() // Разрешение доступа к /api/register без авторизации
                        .requestMatchers("/demo/all").permitAll() // Разрешение доступа к /api/register без авторизации
                        .anyRequest().authenticated() // Все остальные запросы требуют авторизации
                );

        return http.build();
    }


    // Определение PasswordEncoder для хеширования паролей
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.foodsocial.social_media_food.service;

import com.foodsocial.social_media_food.domain.User;
import com.foodsocial.social_media_food.domain.Cookies;
import com.foodsocial.social_media_food.repos.CookieRepository;
import com.foodsocial.social_media_food.security.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Autowired
    private CookieRepository cookieRepository;

    public User ifAuthUser(String token) {
        Cookies cookies = cookieRepository.findByToken(token);
        if (cookies != null && !cookies.isExpired()) {
            return cookies.getUser();
        } else {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    public void addAuthCookie(HttpServletResponse response, String token) {
        Cookie authCookie = new Cookie("token", token);
        authCookie.setHttpOnly(true); // Защита от XSS
        authCookie.setSecure(true); // Передача только по HTTPS
        authCookie.setPath("/"); // Доступна для всего приложения
        authCookie.setMaxAge(60 * 60 * 24 * 30); // Срок действия: 30 дней
        response.addCookie(authCookie);

        // Защита от CSRF
        response.setHeader("Set-Cookie", "token=" + token + "; HttpOnly; Secure; SameSite=Lax");
    }

    public void deleteAuthCookie(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("token", "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);

        // Защита от CSRF
        response.setHeader("Set-Cookie", "token=; HttpOnly; Secure; SameSite=Lax; Max-Age=0");
    }
}

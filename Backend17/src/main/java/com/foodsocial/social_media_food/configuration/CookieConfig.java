package com.foodsocial.social_media_food.configuration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieConfig {
    //TODO
    // ifAuthUser(Token/Cookie)
    // если есть пользователь, то его возвращаем, если нет, то throw new

    public static void addAuthCookie(HttpServletResponse response, String token) {
        Cookie authCookie = new Cookie("token", token);
        authCookie.setHttpOnly(true); // Защищаем от XSS
        authCookie.setSecure(true); // Передача только по HTTPS
        authCookie.setPath("/"); // Доступна для всего приложения
        authCookie.setMaxAge(60 * 60 * 24 * 30); // Срок действия: 30 дней
        response.addCookie(authCookie);

        // Защита от CSRF
        response.setHeader("Set-Cookie", "token=" + token + "; HttpOnly; Secure; SameSite=Lax");
    }

    public static void deleteAuthCookie(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("token", "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);

        // Защита от CSRF
        response.setHeader("Set-Cookie", "token=; HttpOnly; Secure; SameSite=Lax; Max-Age=0");
    }
}


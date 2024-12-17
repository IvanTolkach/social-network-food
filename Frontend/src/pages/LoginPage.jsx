import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next"; // Импортируем хук для перевода

function LoginPage({ onLogin }) {
  const { t } = useTranslation(); // Получаем функцию перевода
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // Состояние для отображения пароля
  const navigate = useNavigate(); // Хук для навигации

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const login = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8080/login",
        {
          identifier,
          password,
        },
        {
          withCredentials: true,
        }
      );

      // Если авторизация успешна, вызываем onLogin с состоянием авторизации
      if (response.status === 200) {
        onLogin(true); // Пользователь авторизован, устанавливаем состояние в true
        navigate("/"); // Перенаправляем на главную страницу
      } else {
        alert(t("Login failed. Please try again.")); // Локализованный текст
      }
    } catch (error) {
      console.error("Ошибка при авторизации:", error);
      alert(t("Invalid credentials. Please try again.")); // Локализованный текст
    }
  };

  const CurrentUserGoogle = () => {
    try {
      handleGoogleLogin();
      axios.get("http://localhost:8080/currentGoogle", {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000",
          "Access-Control-Allow-Credentials": "true",
        },
        withCredentials: true,
      });
    } catch (error) {
      console.error(error);
    }
  };

  const handleGoogleLogin = () => {
    try {
      window.location.href = "http://localhost:8080/login";
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="login-container">
      <h2>{t("LoginCont")}</h2> {/* Локализованный текст */}
      <form
        id="loginForm"
        onSubmit={(e) => {
          e.preventDefault(); // Останавливаем стандартное поведение формы
          login(); // Отправляем данные для авторизации
        }}
      >
        <label htmlFor="identifier">{t("Email:")}</label>{" "}
        {/* Локализованный текст */}
        <input
          type="email"
          id="identifier"
          name="identifier"
          value={identifier}
          onChange={(e) => setIdentifier(e.target.value)}
          required
        />
        <label htmlFor="password">{t("Password:")}</label>{" "}
        {/* Локализованный текст */}
        <input
          type={showPassword ? "text" : "password"} // Условие для отображения пароля
          id="password"
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {/* Переключатель для видимости пароля */}
        <label htmlFor="showPassword">
          <input
            type="checkbox"
            id="showPassword"
            checked={showPassword}
            onChange={togglePasswordVisibility}
          />
          {t("Show password")} {/* Локализованный текст */}
        </label>
        <button type="submit">{t("LoginCont")}</button>{" "}
        {/* Локализованный текст */}
      </form>
      {/* Кнопка для перехода на страницу регистрации */}
      <button
        className="register-btn"
        onClick={() => navigate("/registration_page")} // Навигация на /registration_page
      >
        {t("Go to registration")} {/* Локализованный текст */}
      </button>
      <button onClick={CurrentUserGoogle} className="google-auth-button">
        {t("loginWithGoogle")}
      </button>
    </div>
  );
}

export default LoginPage;

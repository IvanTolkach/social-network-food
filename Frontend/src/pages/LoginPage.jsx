import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function LoginPage({ onLogin }) {
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
        alert("Не удалось авторизоваться. Попробуйте снова.");
      }
    } catch (error) {
      console.error("Ошибка при авторизации:", error);
      alert("Неверные данные. Попробуйте снова.");
    }
  };

  return (
    <div className="login-container">
      <h2>Авторизация</h2>
      <form
        id="loginForm"
        onSubmit={(e) => {
          e.preventDefault(); // Останавливаем стандартное поведение формы
          login(); // Отправляем данные для авторизации
        }}
      >
        <label htmlFor="identifier">Почта:</label>
        <input
          type="email"
          id="identifier"
          name="identifier"
          value={identifier}
          onChange={(e) => setIdentifier(e.target.value)}
          required
        />

        <label htmlFor="password">Пароль:</label>
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
          Показать пароль
        </label>

        <button type="submit">Войти</button>
      </form>

      {/* Кнопка для перехода на страницу регистрации */}
      <button
        className="register-btn"
        onClick={() => navigate("/registration_page")} // Навигация на /registration_page
      >
        Перейти к регистрации
      </button>
    </div>
  );
}

export default LoginPage;

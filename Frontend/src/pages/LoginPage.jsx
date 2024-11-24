import React, { useState } from "react"; // Импортируем React и useState
import axios from "axios";
import { useNavigate } from "react-router-dom"; // Импортируем useNavigate для маршрутизации
import Cookies from "js-cookie"; // Импортируем js-cookie

function LoginPage() {
  const [identifier, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // Состояние для отображения пароля
  const navigate = useNavigate(); // Хук для навигации

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const login = async () => {
    const data = { identifier, password };
    console.log(data);
    try {
      // URL локального бекенда
      console.log(data); // Вывод данных перед отправкой
      const response = await axios.post("http://localhost:8080/login", data); // Замените на правильный порт
      console.log("Ответ от сервера:", response.data);
      alert("Авторизация успешна!");

      // Сохранение токена авторизации в cookie
      Cookies.set("authToken", response.data.token, { expires: 7 }); // Токен хранится 7 дней

      // Пример: перенаправление на главную страницу после успешной авторизации
      navigate("../");
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
          type="identifier"
          id="identifier"
          name="identifier"
          value={identifier}
          onChange={(e) => setEmail(e.target.value)}
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

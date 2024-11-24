import React, { useState } from "react"; // Импортируем React и useState
import axios from "axios";
import { useNavigate } from "react-router-dom"; // Импортируем useNavigate для маршрутизации
import Cookies from "js-cookie"; // Импортируем js-cookie

function RegistrationPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // Состояние для переключателя
  const navigate = useNavigate(); // Хук для навигации

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword); // Переключаем видимость пароля
  };

  const saveData = async () => {
    const data = { username, email, password };

    try {
      // URL локального бекенда
      console.log(data); // Вывод данных перед отправкой
      const response = await axios.post("http://localhost:8080/register", data); // Замените на правильный порт
      console.log("Ответ от сервера:", response.data);
      alert("Регистрация успешна!");

      // Сохранение токена авторизации в cookie
      Cookies.set("authToken", response.data.token, { expires: 7 }); // Токен хранится 7
    } catch (error) {
      console.error("Ошибка при отправке данных:", error);
      alert("Произошла ошибка. Попробуйте снова.");
    }
  };

  return (
    <div className="registration-container">
      <h2>Регистрация</h2>
      <form
        id="registrationForm"
        onSubmit={(e) => {
          e.preventDefault(); // Останавливаем стандартное поведение формы
          saveData(); // Сохраняем данные
        }}
      >
        <label htmlFor="username">Логин:</label>
        <input
          type="text"
          id="username"
          name="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <label htmlFor="email">Почта:</label>
        <input
          type="email"
          id="email"
          name="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <label htmlFor="password">Пароль:</label>
        <input
          type={showPassword ? "text" : "password"} // Если showPassword true, показываем текст
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

        <button type="submit">Зарегистрироваться</button>
      </form>

      {/* Кнопка для перехода на страницу авторизации */}
      <button
        onClick={() => navigate("/login")} // Навигация на /login
        className="auth-button"
      >
        Перейти к авторизации
      </button>
    </div>
  );
}

export default RegistrationPage;

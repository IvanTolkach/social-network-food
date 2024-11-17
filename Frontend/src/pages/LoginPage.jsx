import React, { useState } from "react"; // Импортируем React и useState
import axios from "axios";
import { useNavigate } from "react-router-dom"; // Импортируем useNavigate для маршрутизации

function LoginPage() {
  const [identifier, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate(); // Хук для навигации

  const login = async () => {
    const data = { identifier, password };
    console.log(data);
    try {
      // URL локального бекенда
      console.log(data); // Вывод данных перед отправкой
      const response = await axios.post("http://localhost:8080/login", data); // Замените на правильный порт
      console.log("Ответ от сервера:", response.data);
      alert("Авторизация успешна!");
      // Пример: перенаправление на главную страницу после успешной авторизации
      navigate("/dashboard");
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
          type="password"
          id="password"
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          type="submit"
          style={{
            marginTop: "10px",
            padding: "10px 20px",
            backgroundColor: "#007BFF",
            color: "#fff",
            border: "none",
            borderRadius: "5px",
            cursor: "pointer",
          }}
        >
          Войти
        </button>
      </form>

      {/* Кнопка для перехода на страницу регистрации */}
      <button
        onClick={() => navigate("/registration_page")} // Навигация на /registration_page
        style={{
          marginTop: "20px",
          padding: "10px 20px",
          fontSize: "16px",
          backgroundColor: "#28a745",
          color: "#fff",
          border: "none",
          borderRadius: "5px",
          cursor: "pointer",
        }}
      >
        Перейти к регистрации
      </button>
    </div>
  );
}

export default LoginPage;

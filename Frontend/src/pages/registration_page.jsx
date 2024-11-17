import React, { useState } from "react"; // Импортируем React и useState
import axios from "axios";
import { useNavigate } from "react-router-dom"; // Импортируем useNavigate для маршрутизации

function RegistrationPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate(); // Хук для навигации

  const saveData = async () => {
    const data = { username, email, password };

    try {
      // URL локального бекенда
      console.log(data); // Вывод данных перед отправкой
      const response = await axios.post("http://localhost:8080/register", data); // Замените на правильный порт
      console.log("Ответ от сервера:", response.data);
      alert("Регистрация успешна!");
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
            backgroundColor: "#28a745",
            color: "#fff",
            border: "none",
            borderRadius: "5px",
            cursor: "pointer",
          }}
        >
          Зарегистрироваться
        </button>
      </form>

      {/* Кнопка для перехода на страницу авторизации */}
      <button
        onClick={() => navigate("/login")} // Навигация на /login
        className="auth-button"
        style={{
          marginTop: "20px",
          padding: "10px 20px",
          fontSize: "16px",
          backgroundColor: "#007BFF",
          color: "#fff",
          border: "none",
          borderRadius: "5px",
          cursor: "pointer",
        }}
      >
        Перейти к авторизации
      </button>
    </div>
  );
}

export default RegistrationPage;

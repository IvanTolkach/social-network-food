import React, { useEffect, useState } from "react";
import Cookies from "js-cookie";
//import { useNavigate } from "react-router-dom";
import MainHeader from "../components/mainHeader";
import ContentMenu from "../components/contetnMenu";
import PostBlock from "../components/postBlock";
import { Routes, Route, useLocation } from "react-router-dom";
import RegistrationPage from "../pages/registration_page.jsx";
import LoginPage from "../pages/LoginPage.jsx";
import "../css/app.css";

function MainPage() {
  //const navigate = useNavigate();
  const currentLocation = useLocation();
  const [isAuthenticated, setIsAuthenticated] = useState(false); // Состояние авторизации

  // Проверка токена при загрузке компонента
  useEffect(() => {
    const token = Cookies.get("authToken"); // Получаем токен
    setIsAuthenticated(!!token); // Устанавливаем состояние в зависимости от наличия токена
  }, [currentLocation]);

  // Обработчик выхода
  const handleLogout = () => {
    Cookies.remove("authToken"); // Удаляем токен
    setIsAuthenticated(false); // Обновляем состояние
  };

  return (
    <div className="wrapper">
      {/* Передаём состояние isAuthenticated и функцию handleLogout в MainHeader */}
      {currentLocation.pathname !== "/registration_page" &&
        currentLocation.pathname !== "/login" && (
          <MainHeader
            isAuthenticated={isAuthenticated}
            onLogout={handleLogout}
          />
        )}

      <Routes>
        {/* Главная страница */}
        <Route
          path="/"
          element={
            <div className="content">
              <div className="container">
                <ContentMenu />
                <div className="content__items">
                  <PostBlock />
                </div>
              </div>
            </div>
          }
        />
        {/* Страница регистрации */}
        <Route path="/registration_page" element={<RegistrationPage />} />
        {/* Страница авторизации */}
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </div>
  );
}

export default MainPage;

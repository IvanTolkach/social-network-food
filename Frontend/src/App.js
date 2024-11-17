import "./scss/app.scss";

import React from "react";
import MainHeader from "./components/mainHeader";
import ContentMenu from "./components/contetnMenu";
import PostBlock from "./components/postBlock";
import { Routes, Route, useLocation } from "react-router-dom"; // useLocation для отслеживания текущего пути
import RegistrationPage from "./pages/registration_page.jsx";
import LoginPage from "./pages/LoginPage.jsx"; // Импорт страницы авторизации

function App() {
  // Используем useLocation для получения текущего пути
  const currentLocation = useLocation();

  return (
    <div className="wrapper">
      {/* Отображаем MainHeader только на страницах, где он нужен */}
      {currentLocation.pathname !== "/registration_page" &&
        currentLocation.pathname !== "/login" && <MainHeader />}

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

export default App;

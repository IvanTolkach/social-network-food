import React, { useEffect, useState } from "react";
import axios from "axios";
import { Routes, Route, useNavigate, useLocation } from "react-router-dom";
import MainHeader from "../components/mainHeader";
import ContentMenu from "../components/contentMenu.jsx";
import PostBlock from "../components/postBlock";
import RegistrationPage from "../pages/registration_page.jsx";
import LoginPage from "../pages/LoginPage.jsx";
import "../css/app.css";
import { useTranslation } from "react-i18next"; // Импортируем хук для перевода

function MainPage() {
  const { t } = useTranslation(); // Получаем функцию перевода
  const navigate = useNavigate(); // Навигация для изменения страницы
  const currentLocation = useLocation();
  const [isAuthenticated, setIsAuthenticated] = useState(false); // Булевое состояние для авторизации
  const [posts, setPosts] = useState([]); // Посты, отображаемые на странице

  useEffect(() => {
    // Проверяем текущего пользователя при загрузке страницы
    const checkCurrentUser = async () => {
      try {
        const response = await axios.get("http://localhost:8080/current", {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
            "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
          },
          withCredentials: true, // Разрешаем отправку cookies
        });

        if (response.status === 200) {
          setIsAuthenticated(true); // Пользователь авторизован
          navigate("/"); // Перенаправляем на главную страницу для авторизованных
        }
      } catch (error) {
        console.error(
          "Пользователь не авторизован или произошла ошибка:",
          error
        );
        setIsAuthenticated(false); // Убеждаемся, что состояние авторизации сброшено
      }
    };

    checkCurrentUser();
  }, [navigate]);

  useEffect(() => {
    if (isAuthenticated) {
      fetchUserPosts(); // Загружаем посты при авторизации
    }
  }, [isAuthenticated]);

  // Загрузка постов пользователя
  const fetchUserPosts = async () => {
    try {
      setPosts([]); // Очищаем посты перед загрузкой новых
      const response = await axios.get(
        "http://localhost:8080/posts/our_posts",
        {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
            "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
          },
          withCredentials: true, // Разрешаем отправку cookies
        }
      );
      setPosts(response.data); // Устанавливаем полученные посты
    } catch (error) {
      console.error("Ошибка при загрузке постов пользователя:", error);
    }
  };

  // Загрузка лучших постов
  const fetchBestPosts = async () => {
    try {
      setPosts([]); // Очищаем посты перед загрузкой новых
      const response = await axios.get("http://localhost:8080/posts/3", {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
          "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
        },
        withCredentials: true, // Разрешаем отправку cookies
      });
      setPosts(response.data); // Устанавливаем полученные лучшие посты
    } catch (error) {
      console.error("Ошибка при загрузке лучших постов:", error);
    }
  };

  // Обработчик выхода
  const handleLogout = () => {
    setIsAuthenticated(false); // Меняем состояние на false, чтобы выйти
    setPosts([]); // Очищаем посты при выходе
    axios.head("http://localhost:8080/logout", {
      withCredentials: true, // Разрешаем отправку cookies
    });
  };

  return (
    <div className="wrapper">
      {currentLocation.pathname !== "/registration_page" &&
        currentLocation.pathname !== "/login" && (
          <MainHeader
            isAuthenticated={isAuthenticated}
            onLogout={handleLogout}
          />
        )}

      <Routes>
        <Route
          path="/"
          element={
            <div className="content">
              <div className="container">
                <ContentMenu
                  onFetchBestPosts={fetchBestPosts} // Передаем функцию загрузки лучших постов
                  onFetchUserPosts={fetchUserPosts} // Передаем функцию загрузки постов пользователя
                />
                <div className="content__items">
                  {posts.length > 0 ? (
                    posts.map((post) => (
                      <PostBlock
                        key={post.id}
                        post={post}
                        setPosts={setPosts} // Передаем setPosts для обновления постов
                      />
                    ))
                  ) : (
                    <p>{t("Posts not found.")}</p> // Локализованный текст
                  )}
                </div>
              </div>
            </div>
          }
        />
        <Route path="/registration_page" element={<RegistrationPage />} />
        <Route
          path="/login"
          element={<LoginPage onLogin={setIsAuthenticated} />}
        />
      </Routes>
    </div>
  );
}

export default MainPage;

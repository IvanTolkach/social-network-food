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
import CreatePostForm from "../components/CreatePostForm"; // Импортируем компонент формы создания поста
function MainPage() {
  const { t } = useTranslation(); // Получаем функцию перевода
  const navigate = useNavigate(); // Навигация для изменения страницы
  const currentLocation = useLocation();
  const [isAuthenticated, setIsAuthenticated] = useState(false); // Булевое состояние для авторизации
  const [posts, setPosts] = useState([]); // Посты, отображаемые на странице
  const [activeTab, setActiveTab] = useState("best"); // Храним активную вкладку

  useEffect(() => {
    // Проверяем текущего пользователя только если вкладка "Мои посты" или "Создать пост"
    const checkCurrentUser = async () => {
      if (activeTab === "best") {
        // Если вкладка "Лучшие посты", не проверяем пользователя
        fetchBestPosts(); // Загружаем лучшие посты без проверки авторизации
        return;
      }

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
          fetchUserPosts(); // Загружаем посты пользователя
        }
      } catch (error) {
        console.error(
          "Пользователь не авторизован или произошла ошибка:",
          error
        );
        setIsAuthenticated(false); // Убеждаемся, что состояние авторизации сброшено
        if (activeTab !== "best") {
          navigate("/login"); // Перенаправляем на страницу авторизации, если вкладка не "Лучшие посты"
        }
      }
    };

    // Поменяли условие, чтобы не проверять текущего пользователя, если вкладка "Лучшие посты"
    if (activeTab !== "best") {
      checkCurrentUser();
    } else {
      fetchBestPosts(); // Загружаем лучшие посты
    }
  }, [navigate, activeTab]); // Добавляем activeTab в зависимости

  // Загрузка постов пользователя
  const fetchUserPosts = async () => {
    //console.log("Fetching user posts");
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
      const response = await axios.get(
        "http://localhost:8080/posts/recommendations",
        {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
            "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
          },
          withCredentials: true, // Разрешаем отправку cookies
        }
      );
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

  const sortedPosts = posts.sort(
    (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
  );

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
                  onFetchBestPosts={() => fetchBestPosts()}
                  onFetchUserPosts={() => fetchUserPosts()}
                  isAuthenticated={isAuthenticated}
                  onTabChange={(tab) => setActiveTab(tab)} // Новый пропс
                />
                <div className="content__items">
                  {posts.length > 0 ? (
                    sortedPosts.map((post) => (
                      <PostBlock
                        key={post.id}
                        post={post}
                        setPosts={setPosts}
                        activeTab={activeTab}
                        isAuthenticated={isAuthenticated}
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
        <Route path="/create-post" element={<CreatePostForm />} />{" "}
        {/* Добавляем маршрут для создания поста */}
      </Routes>
    </div>
  );
}

export default MainPage;

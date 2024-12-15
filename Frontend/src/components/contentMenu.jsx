import React, { useState } from "react";
import { useTranslation } from "react-i18next"; // Импортируем хук для перевода
import { useNavigate } from "react-router-dom"; // Импортируем хук для навигации

function ContentMenu({
  onFetchBestPosts,
  onFetchUserPosts,
  isAuthenticated,
  onTabChange,
}) {
  const [activeTab, setActiveTab] = useState("best"); // Храним текущую активную вкладку
  const { t } = useTranslation(); // Получаем функцию перевода
  const navigate = useNavigate(); // Хук для навигации

  const handleTabClick = (tabName, action) => {
    setActiveTab(tabName); // Обновляем локальное состояние
    onTabChange(tabName); // Обновляем состояние в родительском компоненте
    if (action) action(); // Выполняем действие
  };

  const handleMyPostsClick = () => {
    if (!isAuthenticated) {
      // Если пользователь не авторизован, перенаправляем на страницу авторизации
      navigate("/login");
    } else {
      // Если авторизован, загружаем его посты
      handleTabClick("user", onFetchUserPosts);
    }
  };

  const handleCreatePostClick = () => {
    if (!isAuthenticated) {
      // Если пользователь не авторизован, перенаправляем на страницу авторизации
      navigate("/login");
    } else {
      // Если авторизован, показываем уведомление о создании поста
      handleTabClick("create");
      navigate("/create-post");
    }
  };

  return (
    <div className="content__top">
      <div className="categories">
        <ul>
          <li
            className={activeTab === "best" ? "active" : ""}
            onClick={() => handleTabClick("best", onFetchBestPosts)} // При нажатии вызываем fetchBestPosts
          >
            {t("Best Posts")} {/* Перевод текста */}
          </li>
          <li
            className={activeTab === "user" ? "active" : ""}
            onClick={handleMyPostsClick} // Проверяем авторизацию перед загрузкой постов пользователя
          >
            {t("My Posts")} {/* Перевод текста */}
          </li>
          <li
            className={activeTab === "create" ? "active" : ""}
            onClick={handleCreatePostClick} // Проверяем авторизацию перед созданием поста
          >
            {t("Create Post")} {/* Перевод текста */}
          </li>
        </ul>
      </div>
    </div>
  );
}

export default ContentMenu;

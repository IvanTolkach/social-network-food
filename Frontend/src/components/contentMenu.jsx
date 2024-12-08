import React, { useState } from "react";
import { useTranslation } from "react-i18next"; // Импортируем хук для перевода

function ContentMenu({ onFetchBestPosts, onFetchUserPosts }) {
  const [activeTab, setActiveTab] = useState("best"); // Храним текущую активную вкладку
  const { t } = useTranslation(); // Получаем функцию перевода

  // Функция для обработки нажатий на вкладки
  const handleTabClick = (tabName, action) => {
    setActiveTab(tabName); // Устанавливаем активную вкладку
    if (action) action(); // Вызываем переданное действие
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
            onClick={() => handleTabClick("user", onFetchUserPosts)} // При нажатии вызываем fetchUserPosts
          >
            {t("My Posts")} {/* Перевод текста */}
          </li>
          <li
            className={activeTab === "create" ? "active" : ""}
            onClick={() =>
              handleTabClick(
                "create",
                () => alert(t("Create Post Feature Not Implemented")) // Перевод текста
              )
            }
          >
            {t("Create Post")} {/* Перевод текста */}
          </li>
        </ul>
      </div>
    </div>
  );
}

export default ContentMenu;

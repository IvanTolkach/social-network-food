import React, { useState } from "react";

function ContentMenu({ onFetchBestPosts, onFetchUserPosts }) {
  const [activeTab, setActiveTab] = useState("best"); // Храним текущую активную вкладку

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
            Лучшие записи
          </li>
          <li
            className={activeTab === "user" ? "active" : ""}
            onClick={() => handleTabClick("user", onFetchUserPosts)} // При нажатии вызываем fetchUserPosts
          >
            Мои записи
          </li>
          <li
            className={activeTab === "create" ? "active" : ""}
            onClick={() =>
              handleTabClick("create", () =>
                alert("Функция создания записи ещё не реализована.")
              )
            }
          >
            Создать запись
          </li>
        </ul>
      </div>
    </div>
  );
}

export default ContentMenu;

import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import SocialFoodLogo from "../img/Logo.png";
import "../i18n"; // Убедитесь, что путь правильный
import axios from "axios";
import DefaultAvatar from "../img/DefaultAvatar.webp";

function MainHeader({ isAuthenticated, onLogout }) {
  const { t, i18n } = useTranslation();
  const [user, setUser] = useState({
    username: "",
    avatar: DefaultAvatar,
  });
  const [isEditing, setIsEditing] = useState(false);
  const [newUsername, setNewUsername] = useState("");
  const [newAvatar, setNewAvatar] = useState(null);

  const changeLanguage = (language) => {
    i18n.changeLanguage(language);
  };

  useEffect(() => {
    if (isAuthenticated) {
      axios
        .get("http://localhost:8080/current", {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000",
            "Access-Control-Allow-Credentials": "true",
          },
          withCredentials: true,
        })
        .then((response) => {
          setUser({
            username: response.data.username,
            avatar: response.data.avatar || DefaultAvatar,
            id: response.data.id,
          });
          console.log(response.data.username, response.data.id);
        })
        .catch((error) => {
          console.error("Ошибка при получении данных пользователя:", error);
        });
    }
  }, [isAuthenticated]);

  const handleSave = () => {
    // Отправляем обновленные данные пользователя на сервер
    const formData = new FormData();
    formData.append("username", newUsername || user.username);
    if (newAvatar) {
      formData.append("avatar", newAvatar);
    }

    axios
      .put(`http://localhost:8080/user/${user.id}`, formData, {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
          "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
        },
        withCredentials: true, // Обязательно для передачи cookie
      })
      .then(() => {
        setUser({
          username: newUsername || user.username,
          avatar: newAvatar ? URL.createObjectURL(newAvatar) : user.avatar,
        });
        setIsEditing(false);
      })
      .catch((error) => {
        console.error("Ошибка при обновлении профиля:", error);
      });
  };

  return (
    <div className="header">
      <div className="container">
        <div className="header__logo">
          <img width="100" src={SocialFoodLogo} alt="SocialFoodLogo" />
          <div className="SocialFoodLabel">
            <h1>Social Food</h1>
          </div>
        </div>
        <div className="header__cart">
          {isAuthenticated ? (
            <div className="user-info">
              <img
                src={DefaultAvatar}
                alt="User Avatar"
                className="user-avatar"
                onClick={() => setIsEditing(true)}
                style={{ cursor: "pointer" }}
              />
              <span
                className="user-name"
                onClick={() => setIsEditing(true)}
                style={{ cursor: "pointer" }}
              >
                {user.username}
              </span>
              <button onClick={onLogout} className="logout-button">
                {t("Logout")}
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/registration_page" className="button--cart">
                <span>{t("Register")}</span>
              </Link>
              <Link to="/login" className="button--cart">
                <span>{t("Login")}</span>
              </Link>
            </div>
          )}
          <div className="language-switcher">
            <button
              onClick={() => changeLanguage("en")}
              className="language-button"
            >
              EN
            </button>
            <button
              onClick={() => changeLanguage("ru")}
              className="language-button"
            >
              RU
            </button>
            <button
              onClick={() => changeLanguage("by")}
              className="language-button"
            >
              BY
            </button>
          </div>
        </div>
      </div>
      {isEditing && (
        <div className="edit-modal">
          <div className="modal-content">
            <h3>{t("Edit Profile")}</h3>
            <label>
              {t("Username")}:{" "}
              <input
                type="text"
                defaultValue={user.username}
                onChange={(e) => setNewUsername(e.target.value)}
              />
            </label>
            <label>
              {t("Avatar")}:{" "}
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setNewAvatar(e.target.files[0])}
              />
            </label>
            <div className="modal-buttons">
              <button onClick={handleSave}>{t("Save")}</button>
              <button onClick={() => setIsEditing(false)}>{t("Cancel")}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default MainHeader;

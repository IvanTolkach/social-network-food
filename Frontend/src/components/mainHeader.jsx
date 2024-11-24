import React from "react";
import { Link } from "react-router-dom";
import SocialFoodLogo from "../img/Logo.png";

function MainHeader({ isAuthenticated, onLogout }) {
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
            // Если пользователь авторизован, показываем кнопку "Выйти"
            <button
              onClick={onLogout}
              className="button button--cart logout-button"
              style={{
                backgroundColor: "#f44336",
                color: "white",
                border: "none",
                borderRadius: "5px",
                padding: "10px 20px",
                cursor: "pointer",
              }}
            >
              Выйти
            </button>
          ) : (
            // Если пользователь не авторизован, показываем кнопки "Регистрация" и "Вход"
            <div className="auth-buttons">
              <Link to="/registration_page" className="button button--cart">
                <span>Регистрация</span>
              </Link>
              <Link to="/login" className="button button--cart">
                <span>Вход</span>
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default MainHeader;

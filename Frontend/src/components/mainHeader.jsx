import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import SocialFoodLogo from "../img/Logo.png";
import "../i18n"; // Убедитесь, что путь правильный

function MainHeader({ isAuthenticated, onLogout }) {
  const { t, i18n } = useTranslation();

  const changeLanguage = (language) => {
    i18n.changeLanguage(language);
  };

  return (
    <div className="header">
      <div className="container">
        <div className="header__logo">
          <img width="100" src={SocialFoodLogo} alt="SocialFoodLogo" />
          <div className="SocialFoodLabel">
            <h1>Social_Food</h1>
          </div>
        </div>
        <div className="header__cart">
          {isAuthenticated ? (
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
              {t("Logout")}
            </button>
          ) : (
            <div className="auth-buttons">
              <Link to="/registration_page" className="button button--cart">
                <span>{t("Register")}</span>
              </Link>
              <Link to="/login" className="button button--cart">
                <span>{t("Login")}</span>
              </Link>
            </div>
          )}
        </div>
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
  );
}

export default MainHeader;

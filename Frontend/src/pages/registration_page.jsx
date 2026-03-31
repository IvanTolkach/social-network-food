import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

function RegistrationPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordStrength, setPasswordStrength] = useState(0);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const evaluatePasswordStrength = (password) => {
    let strength = 0;
    if (password.length >= 6) strength += 25;
    if (/[A-ZА-Я]/.test(password)) strength += 25;
    if (/[0-9]/.test(password)) strength += 25;
    if (/[@$!%*?&#]/.test(password)) strength += 25;
    return strength;
  };

  const handlePasswordChange = (e) => {
    const newPassword = e.target.value;
    setPassword(newPassword);
    const strength = evaluatePasswordStrength(newPassword);
    setPasswordStrength(strength);
  };

  const saveData = async () => {
    if (passwordStrength < 75) {
      alert(t("passwordTooWeak"));
      return;
    }
    const data = { username, email, password };
    try {
      await axios.post("http://localhost:8080/register", data, {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000",
          "Access-Control-Allow-Credentials": "true",
        },
        withCredentials: true,
      });
      alert(t("registrationSuccess"));
      navigate("../login");
    } catch (error) {
      console.error("Ошибка при отправке данных:", error, data);
      alert(t("errorOccurred"));
    }
  };
  const CurrentUserGoogle = () => {
    try {
      handleGoogleLogin();
      axios.get("http://localhost:8080/currentGoogle", {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000",
          "Access-Control-Allow-Credentials": "true",
        },
        withCredentials: true,
      });
    } catch (error) {
      console.error(error);
    }
  };

  const handleGoogleLogin = () => {
    try {
      window.location.href = "http://localhost:8080/login";
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="registration-container">
      <h2>{t("registration")}</h2>
      <form
        id="registrationForm"
        onSubmit={(e) => {
          e.preventDefault();
          saveData();
        }}
      >
        <label htmlFor="username">{t("login")}:</label>
        <input
          type="text"
          id="username"
          name="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <label htmlFor="email">{t("email")}:</label>
        <input
          type="email"
          id="email"
          name="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <div className="password-container">
          <label htmlFor="password" className="password-label">
            {t("password")}:
          </label>
          <span className="password-status-space"></span>
          <div
            className={`password-status ${
              passwordStrength === 25
                ? "password-very-weak"
                : passwordStrength === 50
                  ? "password-weak"
                  : passwordStrength === 75
                    ? "password-medium"
                    : "password-strong"
            }`}
          >
            {password &&
              (passwordStrength < 25
                ? ""
                : passwordStrength === 25
                  ? t("veryWeak")
                  : passwordStrength === 50
                    ? t("weak")
                    : passwordStrength === 75
                      ? t("medium")
                      : t("strong"))}
          </div>
          <div
            className="password-strength-bar"
            style={{
              marginLeft: "auto",
              marginRight: "0",
              marginBottom: "5px",
              width: "150px",
              height: "8px",
              borderRadius: "4px",
              backgroundColor: "#e0e0e0",
              position: "relative",
              overflow: "hidden",
            }}
          >
            <div
              style={{
                width: `${passwordStrength}%`,
                height: "100%",
                backgroundColor:
                  passwordStrength < 26
                    ? "#ff4d4d"
                    : passwordStrength < 51
                      ? "#ffc107"
                      : passwordStrength < 76
                        ? "#2196F3"
                        : "#28a745",
                transition: "width 0.3s ease, background-color 0.3s ease",
              }}
            ></div>
            {[0, 25, 50, 75].map((div, index) => (
              <div
                key={index}
                style={{
                  position: "absolute",
                  left: `${div}%`,
                  top: "0",
                  bottom: "0",
                  width: "1px",
                  backgroundColor: "#ccc",
                }}
              ></div>
            ))}
          </div>
        </div>
        <input
          type={showPassword ? "text" : "password"}
          id="password"
          name="password"
          value={password}
          onChange={handlePasswordChange}
          required
        />
        <label htmlFor="showPassword">
          <input
            type="checkbox"
            id="showPassword"
            checked={showPassword}
            onChange={togglePasswordVisibility}
          />
          {t("showPassword")}
        </label>
        <button type="submit" disabled={passwordStrength < 75}>
          {t("register")}
        </button>
      </form>
      <button onClick={() => navigate("/login")} className="auth-button">
        {t("goToLogin")}
      </button>
      <button onClick={CurrentUserGoogle} className="google-auth-button">
        {t("registerWithGoogle")}
      </button>
    </div>
  );
}

export default RegistrationPage;

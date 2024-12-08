import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const resources = {
  en: {
    translation: {
      Logout: "Logout",
      Register: "Register",
      Login: "Login",
      "Best Posts": "Best Posts",
      "My Posts": "My Posts",
      "Create Post": "Create Post",
      "Create Post Feature Not Implemented":
        "Create Post Feature Not Implemented",
      "Posts not found.": "Posts not found.",
      LoginCont: "Login",
      "Email:": "Email:",
      "Password:": "Password:",
      "Show password": "Show password",
      "Go to registration": "Go to registration",
      "Login failed. Please try again.": "Login failed. Please try again.",
      "Invalid credentials. Please try again.":
        "Invalid credentials. Please try again.",
      registration: "Registration",
      login: "Login",
      email: "Email",
      password: "Password",
      passwordStrength: "Password Strength",
      showPassword: "Show Password",
      register: "Register",
      goToLogin: "Go to Login",
      passwordTooWeak: "Password must be strong enough.",
      registrationSuccess: "Registration successful!",
      errorOccurred: "An error occurred. Please try again.",
      veryWeak: "Very weak",
      weak: "Weak",
      medium: "Medium",
      strong: "Strong",
    },
  },
  ru: {
    translation: {
      Logout: "Выйти",
      Register: "Регистрация",
      Login: "Вход",
      "Best Posts": "Лучшие записи",
      "My Posts": "Мои записи",
      "Create Post": "Создать запись",
      "Create Post Feature Not Implemented":
        "Функция создания записи ещё не реализована.",
      "Posts not found.": "Посты не найдены.",
      LoginCont: "Авторизация",
      "Email:": "Почта:",
      "Password:": "Пароль:",
      "Show password": "Показать пароль",
      "Go to registration": "Перейти к регистрации",
      "Login failed. Please try again.":
        "Не удалось авторизоваться. Попробуйте снова.",
      "Invalid credentials. Please try again.":
        "Неверные данные. Попробуйте снова.",
      registration: "Регистрация",
      login: "Логин",
      email: "Почта",
      password: "Пароль",
      passwordStrength: "Сила пароля",
      showPassword: "Показать пароль",
      register: "Зарегистрироваться",
      goToLogin: "Перейти к авторизации",
      passwordTooWeak: "Пароль должен быть достаточно сильным.",
      registrationSuccess: "Регистрация успешна!",
      errorOccurred: "Произошла ошибка. Попробуйте снова.",
      veryWeak: "Очень слабый",
      weak: "Слабый",
      medium: "Средний",
      strong: "Сильный",
    },
  },
  by: {
    translation: {
      Logout: "Выйсці",
      Register: "Рэгістрацыя",
      Login: "Уваход",
      "Best Posts": "Лепшыя запісы",
      "My Posts": "Мае запісы",
      "Create Post": "Стварыць запіс",
      "Create Post Feature Not Implemented":
        "Функцыя стварэння запісу яшчэ не рэалізавана.",
      "Posts not found.": "Запісы не знойдзены.",
      LoginCont: "Аўтарызацыя",
      "Email:": "Пошта:",
      "Password:": "Пароль:",
      "Show password": "Паказаць пароль",
      "Go to registration": "Перайсці да рэгістрацыі",
      "Login failed. Please try again.":
        "Не атрымалася ўвайсці. Паспрабуйце зноў.",
      "Invalid credentials. Please try again.":
        "Невірныя дадзеныя. Паспрабуйце зноў.",
      registration: "Рэгістрацыя",
      login: "Лагін",
      email: "Пошта",
      password: "Пароль",
      passwordStrength: "Сіла пароля",
      showPassword: "Паказаць пароль",
      register: "Зарэгістравацца",
      goToLogin: "Перайсці да аўтарызацыі",
      passwordTooWeak: "Пароль павінен быць дастаткова моцным.",
      registrationSuccess: "Рэгістрацыя паспяхова завершана!",
      errorOccurred: "Адбылася памылка. Паспрабуйце зноў.",
      veryWeak: "Вельмі слабы",
      weak: "Слабы",
      medium: "Сярэдні",
      strong: "Моцны",
    },
  },
};

i18n.use(initReactI18next).init({
  resources,
  lng: "ru", // Устанавливаем русский язык по умолчанию
  interpolation: {
    escapeValue: false, // React автоматически экранирует HTML
  },
});

export default i18n;

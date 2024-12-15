import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function CreatePostForm(isAuthenticated) {
  const [title, setTitle] = useState("");
  const [ingredients, setIngredients] = useState("");
  const [image, setImage] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    if (!title.trim() || !ingredients.trim()) {
      alert("Заполните все поля!");
      return;
    }

    // Разделяем ингредиенты по пробелам или запятым и убираем лишние пробелы вокруг каждого элемента
    const ingredientsArray = ingredients
      .split(/[\s,]+/) // Разделение по одному или нескольким пробелам или запятым
      .map((ingredient) => ingredient.trim()) // Убираем пробелы вокруг каждого ингредиента
      .filter((ingredient) => ingredient !== ""); // Убираем пустые строки, если они есть

    const formData = new FormData();
    formData.append("description", title);

    ingredientsArray.forEach((ingredient) => {
      formData.append("ingredients", ingredient);
    });
    if (image) formData.append("image", image);

    try {
      setIsLoading(true);
      await axios.post("http://localhost:8080/posts", formData, {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
          "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
        },
        withCredentials: true, // Разрешаем отправку cookies
      });

      navigate("/");
    } catch (error) {
      console.error("Ошибка при создании поста:", error);
      alert("Произошла ошибка при создании поста. Попробуйте ещё раз.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file && file.size > 2 * 1024 * 1024) {
      alert("Размер файла не должен превышать 2 МБ.");
      return;
    }
    setImage(file);
  };

  return (
    <form onSubmit={handleSubmit} className="create-post-form">
      <h2>Создать пост</h2>
      <div>
        <label htmlFor="title">Текст:</label>
        <textarea
          id="title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          className="create-post-form-textarea"
        />
      </div>
      <div>
        <label htmlFor="ingredients">Ингредиенты (разделяйте запятыми):</label>
        <textarea
          id="ingredients"
          value={ingredients}
          onChange={(e) => setIngredients(e.target.value)}
          required
          className="create-post-form-ingredients"
        />
      </div>
      <div>
        <label htmlFor="image">Изображение:</label>
        <input
          type="file"
          id="image"
          accept="image/*"
          onChange={handleImageChange}
        />
      </div>
      <button type="submit" disabled={isLoading}>
        {isLoading ? "Создание..." : "Создать"}
      </button>
    </form>
  );
}

export default CreatePostForm;

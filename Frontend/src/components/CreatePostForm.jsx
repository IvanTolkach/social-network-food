import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useTranslation } from "react-i18next";
import "../i18n";

function CreatePostForm() {
  const { t } = useTranslation();
  const [title, setTitle] = useState("");
  const [ingredients, setIngredients] = useState("");
  const [image, setImage] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!title.trim() || !ingredients.trim()) {
      alert(t("Fill in all the fields!"));
      return;
    }

    const ingredientsArray = ingredients
      .split(/[\s,]+/)
      .map((ingredient) => ingredient.trim())
      .filter((ingredient) => ingredient !== "");

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
          "Access-Control-Allow-Origin": "http://localhost:3000",
          "Access-Control-Allow-Credentials": "true",
        },
        withCredentials: true,
      });

      navigate("/");
    } catch (error) {
      console.error(t("Error creating post:"), error);
      alert(t("An error occurred while creating the post. Please try again."));
    } finally {
      setIsLoading(false);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file && file.size > 2 * 1024 * 1024) {
      alert(t("File size must not exceed 2 MB."));
      return;
    }
    setImage(file);
  };

  return (
    <form onSubmit={handleSubmit} className="create-post-form">
      <h2>{t("Create a post")}</h2>
      <div>
        <label htmlFor="title">{t("Text:")}</label>
        <textarea
          id="title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          className="create-post-form-textarea"
        />
      </div>
      <div>
        <label htmlFor="ingredients">
          {t("Ingredients (separate with commas):")}
        </label>
        <textarea
          id="ingredients"
          value={ingredients}
          onChange={(e) => setIngredients(e.target.value)}
          required
          className="create-post-form-ingredients"
        />
      </div>
      <div>
        <label htmlFor="image">{t("Image:")}</label>
        <input
          type="file"
          id="image"
          accept="image/*"
          onChange={handleImageChange}
        />
      </div>
      <button type="submit" disabled={isLoading}>
        {isLoading ? t("Creating...") : t("Create")}
      </button>
    </form>
  );
}

export default CreatePostForm;

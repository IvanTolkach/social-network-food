import React, { useState, useEffect } from "react"; // Импортируем React
import "../css/app.css";
import axios from "axios";

function PostBlock({ post }) {
  const [likesCount, setLikesCount] = useState(post.likesCount); // Состояние для лайков
  const [isLiked, setIsLiked] = useState(false); // Состояние для отслеживания лайка
  const [username, setUsername] = useState("Аноним"); // Состояние для имени пользователя
  const [currentUserId, setCurrentUserId] = useState(null); // ID текущего пользователя

  // Загружаем имя пользователя и его ID
  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const response = await axios.get("http://localhost:8080/current", {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
            "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
          },
          withCredentials: true, // Разрешаем отправку cookies
        });

        setUsername(response.data.username || "Аноним");
        setCurrentUserId(response.data.id); // Получаем ID текущего пользователя
      } catch (error) {
        console.error("Ошибка при получении пользователя:", error);
      }
    };

    fetchCurrentUser();
  }, []);

  // Проверка, если текущий пользователь лайкнул пост
  useEffect(() => {
    if (currentUserId && post.likes) {
      const userLiked = post.likes.some(
        (like) => like.userId === currentUserId
      );
      setIsLiked(userLiked); // Устанавливаем состояние в зависимости от того, лайкал ли текущий пользователь
    }
  }, [currentUserId, post.likes]);

  // Обработчик нажатия на кнопку лайка
  const handleLike = async () => {
    if (!post || currentUserId === null) {
      console.error("Post or current user is not defined");
      return; // Проверяем, если post или currentUserId не определены
    }

    try {
      // Сначала сразу обновляем локальные данные на UI, чтобы кнопка и количество лайков обновились мгновенно
      setIsLiked((prevIsLiked) => {
        const newIsLiked = !prevIsLiked;
        setLikesCount((prevLikesCount) =>
          newIsLiked ? prevLikesCount + 1 : prevLikesCount - 1
        );
        return newIsLiked;
      });

      // Отправка запроса на сервер для добавления или снятия лайка
      const response = await axios.post(
        `http://localhost:8080/posts/${post.id}/like`,
        {},
        {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
            "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
          },
          withCredentials: true, // Разрешаем отправку cookies
        }
      );

      // Если запрос прошел успешно, обновляем данные с сервера
      if (response.status === 200) {
        const updatedPost = await axios.get(
          `http://localhost:8080/posts/${post.id}`,
          {
            headers: {
              "Access-Control-Allow-Origin": "http://localhost:3000",
              "Access-Control-Allow-Credentials": "true",
            },
            withCredentials: true,
          }
        );

        // Обновляем количество лайков и проверку, лайкал ли текущий пользователь
        setLikesCount(updatedPost.data.likesCount);
        setIsLiked(
          updatedPost.data.likes.some((like) => like.userId === currentUserId)
        ); // Проверяем, лайкал ли текущий пользователь
      }
    } catch (error) {
      console.error("Ошибка при отправке лайка:", error);
    }
  };

  return (
    <div className="post-block">
      <div className="post-header">
        <img
          className="user-avatar"
          src={post.avatar || "https://example.com/default-avatar.jpg"} // Аватар пользователя
          alt={post.author || "Автор"}
        />
        <h4>{username || "Аноним"}</h4> {/* Имя пользователя */}
      </div>
      <p className="post-text">
        {post.description || "Нет текста для отображения."}
      </p>
      {post.image && (
        <img
          className="post-image"
          src={`http://localhost:8080/uploads/${post.image}`}
          alt="Post"
        />
      )}
      <div className="post-actions">
        {/* Кнопка лайка */}
        <button
          className={`like-button ${isLiked ? "liked" : ""}`} // Добавляем класс "liked", если лайкнут
          onClick={handleLike}
        >
          {isLiked ? "❤️" : "🤍"} {likesCount || 0}
        </button>
        {/* Число комментариев */}
        <button className="comment-button">💬 {post.commentsCount || 0}</button>
        {/* Опциональная кнопка для рецепта, если он есть */}
        {post.ingredients && post.ingredients.length > 0 && (
          <button className="recipe-button">📝 Рецепт</button>
        )}
      </div>
    </div>
  );
}

export default PostBlock;

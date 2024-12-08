import React, { useState, useEffect } from "react";
import "../css/app.css";
import axios from "axios";
import { useTranslation } from "react-i18next"; // Импортируем хук для перевода

function PostBlock({ post }) {
  const [likesCount, setLikesCount] = useState(post.likesCount); // Состояние для лайков
  const [isLiked, setIsLiked] = useState(false); // Состояние для отслеживания лайка
  const [username, setUsername] = useState("Аноним"); // Состояние для имени пользователя
  const [currentUserId, setCurrentUserId] = useState(null); // ID текущего пользователя
  const [comments, setComments] = useState([]); // Состояние для хранения комментариев
  const [isCommentModalOpen, setIsCommentModalOpen] = useState(false); // Состояние для управления модальным окном комментариев
  const [newComment, setNewComment] = useState(""); // Состояние для нового комментария
  const { t } = useTranslation(); // Получаем функцию перевода

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

  const loadComments = async () => {
    try {
      // Загружаем все пользователей
      const usersResponse = await axios.get("http://localhost:8080/all", {
        headers: {
          "Access-Control-Allow-Origin": "http://localhost:3000",
          "Access-Control-Allow-Credentials": "true",
        },
        withCredentials: true,
      });
      const users = usersResponse.data; // Массив всех пользователей

      // Теперь загружаем комментарии
      setComments(post.comments);

      // Для каждого комментария находим имя пользователя
      const commentsWithUsernames = post.comments.map((comment) => {
        const user = users.find((u) => u.id === comment.userId); // Ищем пользователя по userId
        return {
          ...comment,
          username: user ? user.username : "Неизвестный пользователь", // Добавляем имя пользователя
        };
      });

      // Обновляем состояние с комментариями с именами пользователей
      setComments(commentsWithUsernames);
    } catch (error) {
      console.error(
        "Ошибка при загрузке комментариев или пользователей:",
        error
      );
    }
  };

  // Обработчик открытия модального окна
  const handleOpenCommentModal = () => {
    loadComments();
    setIsCommentModalOpen(true);
  };

  // Обработчик закрытия модального окна
  const handleCloseCommentModal = () => {
    setIsCommentModalOpen(false);
  };

  // Обработчик отправки нового комментария
  const handleCommentSubmit = async () => {
    if (newComment.trim() === "") return; // Не отправлять пустой комментарий
    try {
      await axios.post(
        `http://localhost:8080/posts/${post.id}/comment`,
        { description: newComment },
        {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000", // Разрешение для CORS
            "Access-Control-Allow-Credentials": "true", // Разрешение для отправки cookies
          },
          withCredentials: true, // Разрешаем отправку cookies
        }
      );
      setNewComment(""); // Очищаем поле ввода
      loadComments(); // Перезагружаем комментарии
    } catch (error) {
      console.error("Ошибка при отправке комментария:", error);
    }
  };

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
          alt={post.author || t("author")}
        />
        <h4>{username || t("Anonymous")}</h4> {/* Имя пользователя */}
      </div>
      <p className="post-text">{post.description || t("No text available.")}</p>
      {post.image && (
        <img
          className="post-image"
          src={`http://localhost:8080/uploads/${post.image}`}
          alt={t("Post")}
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
        {/* Кнопка комментариев */}
        <button className="comment-button" onClick={handleOpenCommentModal}>
          💬 {post.commentsCount || 0}
        </button>
        {/* Опциональная кнопка для рецепта, если он есть */}
        {post.ingredients && post.ingredients.length > 0 && (
          <button className="recipe-button">📝 {t("Recipe")}</button>
        )}
      </div>

      {/* Модальное окно комментариев */}
      {isCommentModalOpen && (
        <div className="comment-modal">
          <div className="modal-content">
            <button className="close-modal" onClick={handleCloseCommentModal}>
              X
            </button>
            <h3>{t("Comments")}</h3>
            <div className="comments-list">
              {comments.length === 0 ? (
                <p>{t("No comments")}</p>
              ) : (
                comments.map((comment) => (
                  <div key={comment.id} className="comment">
                    <p>
                      <strong>{comment.username}</strong>: {comment.description}
                    </p>
                  </div>
                ))
              )}
            </div>
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder={t("Write a comment...")}
            ></textarea>
            <button onClick={handleCommentSubmit}>{t("Submit")}</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default PostBlock;

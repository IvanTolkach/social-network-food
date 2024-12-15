import React, { useState, useEffect } from "react";
import "../css/app.css";
import axios from "axios";
import { useTranslation } from "react-i18next"; // Импортируем хук для перевода
import DefaultAvatar from "../img/DefaultAvatar.webp";
import { useNavigate } from "react-router-dom";

function PostBlock({ post, activeTab, isAuthenticated }) {
  // Принимаем activeTab как пропс
  const [likesCount, setLikesCount] = useState(post.likesCount); // Состояние для лайков
  const [isLiked, setIsLiked] = useState(false); // Состояние для отслеживания лайка
  const [username, setUsername] = useState("Аноним"); // Состояние для имени пользователя
  const [currentUserId, setCurrentUserId] = useState(null); // ID текущего пользователя
  const [comments, setComments] = useState([]); // Состояние для хранения комментариев
  const [isCommentModalOpen, setIsCommentModalOpen] = useState(false); // Состояние для управления модальным окном комментариев
  const [newComment, setNewComment] = useState(""); // Состояние для нового комментария
  const { t } = useTranslation(); // Получаем функцию перевода
  const [postAuthor, setPostAuthor] = useState([]); // Состояние для авторов постов
  const navigate = useNavigate();
  const [commentsCount, setCommentsCount] = useState(post.commentsCount || 0);
  const [isCommentsLoaded, setIsCommentsLoaded] = useState(false);
  const [isIngredientModalOpen, setIsIngredientModalOpen] = useState(false); // Состояние для модального окна ингредиентов

  useEffect(() => {
    // Проверяем, что activeTab задан и не равен "best"
    if (activeTab !== "best" || isAuthenticated === true) {
      const fetchCurrentUser = async () => {
        try {
          //console.log("Fetching user for tab:", activeTab); // Для отладки
          const response = await axios.get("http://localhost:8080/current", {
            headers: {
              "Access-Control-Allow-Origin": "http://localhost:3000",
              "Access-Control-Allow-Credentials": "true",
            },
            withCredentials: true,
          });
          setUsername(response.data.username || "Аноним");
          setCurrentUserId(response.data.id);
        } catch (error) {
          console.error("Ошибка при получении пользователя:", error);
        }
      };
      ////////////
      fetchCurrentUser();
      //console.log("Active tab is 'best'. Fetching author for the post..."); // Для отладки
      const fetchAuthorForPost = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8080/user/${post.userId}`
          );
          const author = {
            id: post.userId,
            username: response.data.username,
            avatar: `http://localhost:8080${response.data.avatar}`, // Составляем полный путь
          };
          console.log(response.data);
          setPostAuthor(author); // Устанавливаем автора в состояние как массив
          //console.log("Author fetched successfully:", author); // Для отладки
        } catch (error) {
          console.error("Ошибка при загрузке автора поста:", error);
        }
      };

      fetchAuthorForPost();
    }
  }, [activeTab, post, isAuthenticated]); // Условие зависит от activeTab и массива post

  // Функция для загрузки комментариев при открытии модального окна
  const loadComments = async () => {
    try {
      // Загружаем комментарии из текущего поста
      const comments = post.comments;

      // Получаем уникальные userId из комментариев
      const uniqueUserIds = [
        ...new Set(comments.map((comment) => comment.userId)),
      ];

      // Загружаем данные для каждого пользователя
      const userRequests = uniqueUserIds.map((userId) =>
        axios.get(`http://localhost:8080/user/${userId}`, {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000",
            "Access-Control-Allow-Credentials": "true",
          },
          withCredentials: true,
        })
      );

      // Выполняем все запросы
      const userResponses = await Promise.all(userRequests);

      // Преобразуем ответы в объект { userId: userData }
      const users = userResponses.reduce((acc, response) => {
        const user = response.data;
        acc[user.id] = user;
        return acc;
      }, {});

      // Добавляем имена пользователей к комментариям
      const commentsWithUsernames = comments.map((comment) => {
        const user = users[comment.userId];
        return {
          ...comment,
          username: user ? user.username : "Неизвестный пользователь",
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
    if (!post || currentUserId === null) {
      // Перенаправление на страницу авторизации, если пользователь не авторизован
      navigate("/login");
      return; // Прерываем выполнение функции
    }

    // Загружаем комментарии только один раз
    if (!isCommentsLoaded) {
      loadComments();
      setIsCommentsLoaded(true); // Устанавливаем флаг, что комментарии загружены
    }

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
      const response = await axios.post(
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
      // Создаем новый комментарий с данными, полученными с сервера
      const newCommentData = response.data; // Считаем, что сервер возвращает данные нового комментария

      // Обновляем локальные данные
      setComments((prevComments) => [
        ...prevComments,
        { ...newCommentData, username: username || "Неизвестный пользователь" }, // Добавляем новый комментарий
      ]);

      // Обновляем счетчик комментариев
      setCommentsCount((prevCount) => prevCount + 1); // Увеличиваем количество комментариев

      setNewComment(""); // Очищаем поле ввода
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
      // Перенаправление на страницу авторизации, если пользователь не авторизован
      navigate("/login");
      return; // Прерываем выполнение функции
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

  const handleOpenIngredientModal = () => {
    if (post.ingredients && post.ingredients.length > 0) {
      setIsIngredientModalOpen(true);
    }
  };

  const handleCloseIngredientModal = () => {
    setIsIngredientModalOpen(false);
  };

  // Удаляем начальный путь из post.image
  const imagePath = post.image.replace(/^\/uploads\//, "");
  return (
    <div className="post-block">
      <div className="post-header">
        <img
          className="user-avatar"
          src={DefaultAvatar}
          alt={postAuthor.username || t("author")}
        />
        <h4>
          {activeTab === "best" && isAuthenticated === false
            ? postAuthor?.username || t("Anonymous")
            : username || t("Anonymous")}
        </h4>
        {/* Имя пользователя */}
      </div>
      <p className="post-text">{post.description || t("No text available.")}</p>
      {post.image && (
        <img
          className="post-image"
          src={`http://localhost:8080/upload/photo/${imagePath}`}
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
          💬 {commentsCount || 0}
        </button>
        {/* Кнопка для рецепта, если он есть */}
        {post.ingredients && post.ingredients.length > 0 && (
          <button className="recipe-button" onClick={handleOpenIngredientModal}>
            📝 {t("Recipe")}
          </button>
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
      {isIngredientModalOpen && (
        <div className="ingredients-modal-overlay">
          <div className="ingredients-modal-content">
            <button
              className="close-ingredients-modal"
              onClick={handleCloseIngredientModal}
            >
              X
            </button>
            <h3>Ингредиенты</h3>
            <ul className="ingredients-list">
              {post.ingredients.map((ingredient, index) => (
                <li key={index}>{ingredient}</li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}

export default PostBlock;

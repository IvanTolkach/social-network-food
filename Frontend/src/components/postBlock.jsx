import React, { useState, useEffect } from "react";
import "../css/app.css";
import axios from "axios";
import { useTranslation } from "react-i18next";
import DefaultAvatar from "../img/DefaultAvatar.webp";
import { useNavigate } from "react-router-dom";

function PostBlock({ post, activeTab, isAuthenticated }) {
  const [likesCount, setLikesCount] = useState(post.likesCount);
  const [isLiked, setIsLiked] = useState(false);
  const [username, setUsername] = useState("Аноним");
  const [currentUserId, setCurrentUserId] = useState(null);
  const [comments, setComments] = useState([]);
  const [isCommentModalOpen, setIsCommentModalOpen] = useState(false);
  const [newComment, setNewComment] = useState("");
  const { t } = useTranslation();
  const [postAuthor, setPostAuthor] = useState([]);
  const navigate = useNavigate();
  const [commentsCount, setCommentsCount] = useState(post.commentsCount || 0);
  const [isCommentsLoaded, setIsCommentsLoaded] = useState(false);
  const [isIngredientModalOpen, setIsIngredientModalOpen] = useState(false);

  const fetchCurrentUser = async () => {
    try {
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

  useEffect(() => {
    if (isAuthenticated) {
      fetchCurrentUser();
    }

    if (activeTab === "best") {
      const fetchAuthorForPost = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8080/user/${post.user_id.id}`
          );
          const author = {
            id: post.user_id.id,
            username: response.data.username,
            avatar: `http://localhost:8080${response.data.avatar}`,
          };
          setPostAuthor(author);
        } catch (error) {
          console.error("Ошибка при загрузке автора поста:", error);
        }
      };
      fetchAuthorForPost();
    }
  }, [activeTab, post, isAuthenticated]);

  const loadComments = async () => {
    try {
      const comments = post.comments;
      const uniqueUserIds = [
        ...new Set(comments.map((comment) => comment.user.id)),
      ];

      const userRequests = uniqueUserIds.map((userId) =>
        axios.get(`http://localhost:8080/user/${userId}`, {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000",
            "Access-Control-Allow-Credentials": "true",
          },
          withCredentials: true,
        })
      );

      const userResponses = await Promise.all(userRequests);
      const users = userResponses.reduce((acc, response) => {
        const user = response.data;
        acc[user.id] = user;
        return acc;
      }, {});

      const commentsWithUsernames = comments.map((comment) => {
        const user = users[comment.user.id];
        return {
          ...comment,
          username: user ? user.username : "Неизвестный пользователь",
        };
      });

      setComments(commentsWithUsernames);
    } catch (error) {
      console.error(
        "Ошибка при загрузке комментариев или пользователей:",
        error
      );
    }
  };

  const handleOpenCommentModal = () => {
    if (!post || currentUserId === null) {
      navigate("/login");
      return;
    }

    if (!isCommentsLoaded) {
      loadComments();
      setIsCommentsLoaded(true);
    }

    setIsCommentModalOpen(true);
  };

  const handleCloseCommentModal = () => {
    setIsCommentModalOpen(false);
  };

  const handleCommentSubmit = async () => {
    if (newComment.trim() === "") return;
    try {
      const response = await axios.post(
        `http://localhost:8080/posts/${post.id}/comment`,
        { description: newComment },
        {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000",
            "Access-Control-Allow-Credentials": "true",
          },
          withCredentials: true,
        }
      );
      const newCommentData = response.data;

      setComments((prevComments) => [
        ...prevComments,
        { ...newCommentData, username: username || "Неизвестный пользователь" },
      ]);

      setCommentsCount((prevCount) => prevCount + 1);
      setNewComment("");
    } catch (error) {
      console.error("Ошибка при отправке комментария:", error);
    }
  };

  useEffect(() => {
    if (currentUserId && post.likes) {
      const userLiked = post.likes.some(
        (like) => like.user.id === currentUserId
      );
      setIsLiked(userLiked);
    }
  }, [currentUserId, post.likes]);

  const handleLike = async () => {
    if (!post || currentUserId === null) {
      navigate("/login");
      return;
    }
    try {
      setIsLiked((prevIsLiked) => {
        const newIsLiked = !prevIsLiked;
        setLikesCount((prevLikesCount) =>
          newIsLiked ? prevLikesCount + 1 : prevLikesCount - 1
        );
        return newIsLiked;
      });

      const response = await axios.post(
        `http://localhost:8080/posts/${post.id}/like`,
        {},
        {
          headers: {
            "Access-Control-Allow-Origin": "http://localhost:3000",
            "Access-Control-Allow-Credentials": "true",
          },
          withCredentials: true,
        }
      );

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
        setLikesCount(updatedPost.data.likesCount);
        setIsLiked(
          updatedPost.data.likes.some((like) => like.user.id === currentUserId)
        );
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
          {activeTab === "best"
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
                <p>{t("Nocomments")}</p>
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
              placeholder={t("WriteAcomment")}
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
            <h3>{t("Ingredients")}</h3>
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

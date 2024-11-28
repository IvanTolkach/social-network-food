import React from "react"; // Импортируем React

function PostBlock() {
  return (
    <div className="post-block">
      <div className="post-header">
        <img
          className="user-avatar"
          src="https://example.com/user-avatar.jpg" // Здесь будет изображение пользователя
          alt="User"
        />
        <h4>Имя пользователя</h4>
      </div>
      <p className="post-text">
        Какой-то текст для поста, который может быть длинным и обрезаться.
      </p>
      <img className="post-image" src="" alt="Post" />
      <div className="post-actions">
        <button className="like-button">🤍 120</button> {/* Число лайков */}
        <button className="comment-button">💬 45</button>{" "}
        {/* Число комментариев */}
        <button className="recipe-button">📝 Рецепт</button>{" "}
        {/* Опциональная кнопка */}
      </div>
    </div>
  );
}

export default PostBlock;

# Этап сборки
FROM maven:3.8.4-openjdk-17-slim AS build

# Установка рабочей директории в контейнере
WORKDIR /app

# Копирование файлов проекта в контейнер
COPY pom.xml .
COPY src ./src

# Сборка проекта
RUN mvn clean package

# Этап выполнения
FROM openjdk:17-slim

# Установка рабочей директории в контейнере
WORKDIR /app

# Копирование собранного jar файла из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Открытие порта для приложения
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

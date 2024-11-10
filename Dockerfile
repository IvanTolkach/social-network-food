FROM openjdk:23
COPY /target/*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
# Используем официальный OpenJDK образ
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Настраиваем кодировку UTF-8 (перенесено в начало)
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

# Копируем Maven wrapper и pom.xml для кеширования зависимостей
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Делаем mvnw исполняемым
RUN chmod +x ./mvnw

# Скачиваем зависимости (будут закешированы если pom.xml не изменился)
RUN ./mvnw dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Переименовываем JAR файл в фиксированное имя для удобства
RUN mv target/kafka-demo-*.jar target/app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение (используем фиксированное имя файла)
CMD ["java", "-jar", "target/app.jar"]
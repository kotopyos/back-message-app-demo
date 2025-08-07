# Kafka Demo Application

Простое Java Spring Boot приложение для демонстрации работы с Apache Kafka.

## Функциональность

- **Producer**: Отправка сообщений в Kafka топик через REST API
- **Consumer**: Обработка сообщений из Kafka топика
- **REST API**: Endpoints для отправки сообщений
- **JSON сериализация**: Автоматическое преобразование объектов в JSON

## Структура проекта

```
src/main/java/com/example/kafkademo/
├── KafkaDemoApplication.java      # Главный класс приложения
├── config/
│   └── KafkaTopicConfig.java      # Конфигурация Kafka топиков
├── controller/
│   └── MessageController.java     # REST контроллер
├── model/
│   └── Message.java               # Модель сообщения
└── service/
    ├── MessageProducer.java       # Kafka Producer
    └── MessageConsumer.java       # Kafka Consumer
```

## Запуск приложения

### Вариант 1: Локальная Kafka (Docker Compose)

#### 1. Запустить Kafka с помощью Docker Compose

```bash
docker-compose up -d
```

Это запустит:
- Zookeeper на порту 2181
- Kafka на порту 9092
- Kafka UI на порту 8090 (http://localhost:8090)

#### 2. Собрать и запустить Spring Boot приложение

```bash
mvn clean install
mvn spring-boot:run
```

### Вариант 2: Удаленная Kafka на ВМ

#### 1. Настройка Kafka на ВМ

На вашей ВМ в конфигурации Kafka (`server.properties`) убедитесь, что настроены listeners:

```properties
# Разрешаем внешние подключения
listeners=PLAINTEXT://0.0.0.0:9092
advertised.listeners=PLAINTEXT://YOUR_VM_IP:9092
```

#### 2. Настройка firewall на ВМ

```bash
# Ubuntu/Debian
sudo ufw allow 9092/tcp

# CentOS/RHEL
sudo firewall-cmd --permanent --add-port=9092/tcp
sudo firewall-cmd --reload
```

#### 3. Проверка подключения

```bash
# Сделайте скрипт исполняемым
chmod +x test-kafka-connection.sh

# Отредактируйте IP-адрес в скрипте и запустите
./test-kafka-connection.sh
```

#### 4. Запуск приложения с удаленным профилем

**Способ 1: Переменная окружения**
```bash
export KAFKA_BOOTSTRAP_SERVERS=192.168.1.100:9092
mvn spring-boot:run
```

**Способ 2: Профиль remote**
```bash
# Отредактируйте IP в application-remote.yml
mvn spring-boot:run -Dspring.profiles.active=remote
```

**Способ 3: Параметр командной строки**
```bash
mvn spring-boot:run -Dspring.kafka.bootstrap-servers=192.168.1.100:9092
```

## API Endpoints

### Отправить простое сообщение
```bash
POST http://localhost:8080/api/messages/send
Content-Type: application/json

{
    "content": "Привет, Kafka!",
    "sender": "Пользователь1"
}
```

### Отправить полное сообщение
```bash
POST http://localhost:8080/api/messages/send-object
Content-Type: application/json

{
    "id": "msg-123",
    "content": "Детальное сообщение",
    "sender": "Система",
    "timestamp": "2025-07-19T10:30:00"
}
```

### Проверить состояние сервиса
```bash
GET http://localhost:8080/api/messages/health
```

## Примеры использования

### Отправка сообщения через curl

```bash
curl -X POST http://localhost:8080/api/messages/send \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Тестовое сообщение",
    "sender": "TestUser"
  }'
```

### Ответ от сервера

```json
{
    "status": "success",
    "message": "Сообщение отправлено в Kafka"
}
```

## Мониторинг

1. **Kafka UI**: http://localhost:8090 - веб-интерфейс для мониторинга Kafka
2. **Логи приложения**: Все события producer/consumer логируются в консоль
3. **REST API**: Endpoint `/api/messages/health` для проверки состояния

## Конфигурация

Основные настройки в `application.yml`:

- **Kafka Bootstrap Servers**: localhost:9092
- **Consumer Group**: demo-group
- **Topic Name**: demo-topic
- **Serialization**: JSON

## Особенности реализации

- **Асинхронная обработка**: Producer использует CompletableFuture
- **Manual Acknowledgment**: Consumer подтверждает обработку вручную
- **Error Handling**: Обработка ошибок на всех уровнях
- **JSON Support**: Автоматическая сериализация/десериализация
- **Auto Topic Creation**: Топик создается автоматически при первом запуске

## Остановка приложения

```bash
# Остановить Spring Boot приложение
Ctrl+C

# Остановить Kafka и Zookeeper
docker-compose down
```
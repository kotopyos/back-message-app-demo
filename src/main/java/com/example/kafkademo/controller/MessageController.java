package com.example.kafkademo.controller;

import com.example.kafkademo.model.Message;
import com.example.kafkademo.service.MessageProducer;
import com.example.kafkademo.service.MessageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*") // Разрешаем CORS для frontend
public class MessageController {

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MessageStorageService messageStorageService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody MessageRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            messageProducer.sendMessage(request.getContent(), request.getSender());


            response.put("status", "success");
            response.put("message", "Сообщение отправлено в Kafka");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Ошибка при отправке сообщения: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/send-object")
    public ResponseEntity<Map<String, String>> sendMessageObject(@RequestBody Message message) {
        try {
            messageProducer.sendMessage(message);
            messageStorageService.addMessage(message);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Сообщение отправлено в Kafka");
            response.put("messageId", message.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Ошибка при отправке сообщения: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Получить все сообщения из хранилища
     */
    @GetMapping("/list")
    public ResponseEntity<MessagesResponse> getAllMessages() {
        try {
            List<Message> messages = messageStorageService.getAllMessages();
            MessagesResponse response = new MessagesResponse(
                    "success",
                    messages,
                    messages.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MessagesResponse response = new MessagesResponse(
                    "error",
                    null,
                    0
            );
            response.setError("Ошибка при получении сообщений: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Получить последние N сообщений
     */
    @GetMapping("/recent")
    public ResponseEntity<MessagesResponse> getRecentMessages(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            List<Message> messages = messageStorageService.getRecentMessages(limit);
            MessagesResponse response = new MessagesResponse(
                    "success",
                    messages,
                    messages.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MessagesResponse response = new MessagesResponse(
                    "error",
                    null,
                    0
            );
            response.setError("Ошибка при получении сообщений: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Получить сообщения по отправителю
     */
    @GetMapping("/by-sender/{sender}")
    public ResponseEntity<MessagesResponse> getMessagesBySender(@PathVariable String sender) {
        try {
            List<Message> messages = messageStorageService.getMessagesBySender(sender);
            MessagesResponse response = new MessagesResponse(
                    "success",
                    messages,
                    messages.size()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MessagesResponse response = new MessagesResponse(
                    "error",
                    null,
                    0
            );
            response.setError("Ошибка при получении сообщений: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Получить статистику сообщений
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMessageStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", messageStorageService.getMessageCount());
        stats.put("maxCapacity", 100);
        stats.put("status", "active");
        return ResponseEntity.ok(stats);
    }

    /**
     * Очистить все сообщения
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearMessages() {
        Map<String, String> response = new HashMap<>();
        try {
            messageStorageService.clearMessages();
            response.put("status", "success");
            response.put("message", "Все сообщения очищены");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Ошибка при очистке сообщений: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "Kafka Demo Application");
        response.put("messageCount", String.valueOf(messageStorageService.getMessageCount()));
        return ResponseEntity.ok(response);
    }

    // DTO для запроса
    public static class MessageRequest {
        private String content;
        private String sender;

        public MessageRequest() {}

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }
    }

    // DTO для ответа со списком сообщений
    public static class MessagesResponse {
        private String status;
        private List<Message> messages;
        private int count;
        private String error;

        public MessagesResponse() {}

        public MessagesResponse(String status, List<Message> messages, int count) {
            this.status = status;
            this.messages = messages;
            this.count = count;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
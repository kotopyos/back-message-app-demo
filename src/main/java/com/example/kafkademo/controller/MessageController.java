package com.example.kafkademo.controller;

import com.example.kafkademo.model.Message;
import com.example.kafkademo.service.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    @Autowired
    private MessageProducer messageProducer;
    
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
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "Kafka Demo Application");
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
}
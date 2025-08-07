package com.example.kafkademo.service;

import com.example.kafkademo.model.Message;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class MessageStorageService {
    
    // Храним последние 100 сообщений в памяти
    private static final int MAX_MESSAGES = 100;
    private final Deque<Message> messages = new ConcurrentLinkedDeque<>();
    
    /**
     * Добавляет сообщение в хранилище
     */
    public void addMessage(Message message) {
        messages.addFirst(message);
        
        // Удаляем старые сообщения, если превышен лимит
        while (messages.size() > MAX_MESSAGES) {
            messages.removeLast();
        }
    }
    
    /**
     * Получает все сообщения
     */
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    /**
     * Получает последние N сообщений
     */
    public List<Message> getRecentMessages(int limit) {
        return messages.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Получает сообщения по отправителю
     */
    public List<Message> getMessagesBySender(String sender) {
        return messages.stream()
                .filter(msg -> msg.getSender().equals(sender))
                .collect(Collectors.toList());
    }
    
    /**
     * Очищает все сообщения
     */
    public void clearMessages() {
        messages.clear();
    }
    
    /**
     * Получает количество сообщений
     */
    public int getMessageCount() {
        return messages.size();
    }
}
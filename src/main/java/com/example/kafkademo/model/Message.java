package com.example.kafkademo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {

    private String id;

    @JsonProperty("message")
    @JsonAlias({"content", "message"})
    private String content;

    @JsonProperty("username")
    @JsonAlias({"sender", "username"})
    private String sender;

    private String timestamp;

    // Конструкторы
    public Message() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Message(String id, String content, String sender) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Message(String id, String content, String sender, String timestamp) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Для совместимости с frontend - возвращаем content как message
    @JsonProperty("message")
    public String getMessage() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // Для совместимости с frontend - возвращаем sender как username
    @JsonProperty("username")
    public String getUsername() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
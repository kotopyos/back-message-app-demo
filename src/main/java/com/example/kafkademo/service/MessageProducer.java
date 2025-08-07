package com.example.kafkademo.service;

import com.example.kafkademo.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    private static final String TOPIC_NAME = "demo-topic";

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(Message message) {
        logger.info("Отправка сообщения: {}", message);

        CompletableFuture<SendResult<String, Message>> future =
            kafkaTemplate.send(TOPIC_NAME, message.getId(), message);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Сообщение отправлено успешно: offset={}",
                    result.getRecordMetadata().offset());
            } else {
                logger.error("Ошибка при отправке сообщения", exception);
            }
        });
    }

    public void sendMessage(String content, String sender) {
        Message message = new Message(
            java.util.UUID.randomUUID().toString(),
            content,
            sender
        );
        sendMessage(message);
    }
}
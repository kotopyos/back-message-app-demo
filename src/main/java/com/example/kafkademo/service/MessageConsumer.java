package com.example.kafkademo.service;

import com.example.kafkademo.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private MessageStorageService messageStorageService;

    @KafkaListener(topics = "demo-topic", groupId = "demo-group")
    public void listen(@Payload Message message,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                       @Header(KafkaHeaders.OFFSET) long offset,
                       Acknowledgment acknowledgment) {

        logger.info("Получено сообщение из топика: {}, раздел: {}, offset: {}",
                topic, partition, offset);
        logger.info("Содержимое сообщения: {}", message);

        try {
            // Сохраняем сообщение в хранилище
            messageStorageService.addMessage(message);

            // Здесь можно добавить бизнес-логику обработки сообщения
            processMessage(message);

            // Подтверждаем обработку сообщения
            acknowledgment.acknowledge();
            logger.info("Сообщение обработано и сохранено успешно: {}", message.getId());

        } catch (Exception e) {
            logger.error("Ошибка при обработке сообщения: {}", message.getId(), e);
            // В реальном приложении здесь можно добавить логику повторной обработки
        }
    }

    private void processMessage(Message message) {
        // Симуляция обработки сообщения
        logger.info("Обрабатываем сообщение от {}: {}",
                message.getSender(), message.getContent());

        // Здесь может быть любая бизнес-логика:
        // - сохранение в базу данных
        // - отправка email
        // - вызов внешних API
        // - и т.д.
    }
}
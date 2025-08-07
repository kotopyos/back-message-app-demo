#!/bin/bash

# Скрипт для проверки подключения к Kafka на ВМ
# Замените IP_ADDRESS на адрес вашей ВМ

VM_IP="192.168.0.112"  # Замените на IP вашей ВМ
KAFKA_PORT="9092"

echo "Проверка подключения к Kafka на $VM_IP:$KAFKA_PORT..."

# Проверка доступности порта
if nc -z $VM_IP $KAFKA_PORT; then
    echo "✅ Порт $KAFKA_PORT доступен на $VM_IP"
else
    echo "❌ Порт $KAFKA_PORT недоступен на $VM_IP"
    echo "Проверьте:"
    echo "1. Kafka запущена на ВМ"
    echo "2. Firewall настроен правильно"
    echo "3. Kafka listener настроен для внешних подключений"
    exit 1
fi

# Проверка с помощью telnet (альтернативный способ)
echo "Проверка с помощью telnet..."
timeout 5 telnet $VM_IP $KAFKA_PORT || echo "Telnet недоступен или подключение не удалось"

echo "Для тестирования Kafka можно использовать команды:"
echo "# Список топиков:"
echo "kafka-topics --bootstrap-server $VM_IP:$KAFKA_PORT --list"
echo ""
echo "# Создание топика:"
echo "kafka-topics --bootstrap-server $VM_IP:$KAFKA_PORT --create --topic demo-topic --partitions 3 --replication-factor 1"
echo ""
echo "# Producer test:"
echo "kafka-console-producer --bootstrap-server $VM_IP:$KAFKA_PORT --topic demo-topic"
echo ""
echo "# Consumer test:"
echo "kafka-console-consumer --bootstrap-server $VM_IP:$KAFKA_PORT --topic demo-topic --from-beginning"
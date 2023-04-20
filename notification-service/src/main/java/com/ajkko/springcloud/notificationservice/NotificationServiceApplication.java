package com.ajkko.springcloud.notificationservice;

import com.ajkko.springcloud.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(OrderCreatedEvent orderCreatedEvent) {
        log.info("Notification service. Order is Created " + orderCreatedEvent.getOrderNumber());
    }

}

package com.ajkko.springcloud.orderservice.service;

import com.ajkko.springcloud.orderservice.dto.mapper.OrderMapper;
import com.ajkko.springcloud.orderservice.dto.request.OrderRequest;
import com.ajkko.springcloud.orderservice.dto.response.InventoryResponse;
import com.ajkko.springcloud.orderservice.dto.response.OrderResponse;
import com.ajkko.springcloud.orderservice.entity.Order;
import com.ajkko.springcloud.orderservice.entity.OrderLineItem;
import com.ajkko.springcloud.orderservice.event.OrderCreatedEvent;
import com.ajkko.springcloud.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public Long createOrder(OrderRequest order) {

        Order createdOrder = orderMapper.map(order);
        createdOrder.setOrderNumber(UUID.randomUUID().toString());

        List<String> skuCodes = createdOrder.getOrderLineItems().stream().map(OrderLineItem::getSkuCode).toList();

        InventoryResponse[] inventories = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventories",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()
                )
                .retrieve().bodyToMono(InventoryResponse[].class)
                .block();



        boolean allProductsInStock = inventories != null
                && inventories.length > 0
                && Arrays.stream(inventories).allMatch(InventoryResponse::isInStock);

        if (!allProductsInStock) {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

        orderRepository.save(createdOrder);
        kafkaTemplate.send("notificationTopic", new OrderCreatedEvent(createdOrder.getOrderNumber()));
        log.info("Order {} is saved", createdOrder.getId());
        return createdOrder.getId();
    }

    public List<OrderResponse> getOrders() {
        return orderMapper.map(
                orderRepository.findAll()
        );
    }

    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::map)
                .orElse(null);
    }

    public void removeOrder(Long id) {
        orderRepository.deleteById(id);
        log.info("Order {} is removed", id);
    }
}

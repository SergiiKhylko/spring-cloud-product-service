package com.ajkko.springcloud.orderservice.service;

import com.ajkko.springcloud.orderservice.dto.mapper.OrderMapper;
import com.ajkko.springcloud.orderservice.dto.request.OrderRequest;
import com.ajkko.springcloud.orderservice.dto.response.OrderResponse;
import com.ajkko.springcloud.orderservice.entity.Order;
import com.ajkko.springcloud.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Long createOrder(OrderRequest order) {

        Order createdOrder = orderMapper.map(order);
        createdOrder.setOrderNumber(UUID.randomUUID().toString());

        orderRepository.save(createdOrder);
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

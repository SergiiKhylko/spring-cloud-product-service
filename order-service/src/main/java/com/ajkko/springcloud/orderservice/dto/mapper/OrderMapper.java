package com.ajkko.springcloud.orderservice.dto.mapper;

import com.ajkko.springcloud.orderservice.dto.request.OrderLineItemRequest;
import com.ajkko.springcloud.orderservice.dto.request.OrderRequest;
import com.ajkko.springcloud.orderservice.dto.response.OrderLineItemResponse;
import com.ajkko.springcloud.orderservice.dto.response.OrderResponse;
import com.ajkko.springcloud.orderservice.entity.Order;
import com.ajkko.springcloud.orderservice.entity.OrderLineItem;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class OrderMapper {
    public Order map(OrderRequest order) {
        return Order.builder()
                .orderLineItems(mapOrderLineItemsRequest(order.getOrderLineItems()))
                .build();
    }

    public OrderResponse map(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderLineItems(mapOrderLineItems(order.getOrderLineItems()))
                .build();
    }

    public OrderLineItem map(OrderLineItemRequest orderLineItem) {
        return OrderLineItem.builder()
                .skuCode(orderLineItem.getSkuCode())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    public OrderLineItemResponse map(OrderLineItem orderLineItem) {
        return OrderLineItemResponse.builder()
                .id(orderLineItem.getId())
                .skuCode(orderLineItem.getSkuCode())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    public List<OrderResponse> map(Collection<Order> orders) {
        return orders.stream().map(this::map).toList();
    }

    private List<OrderLineItemResponse> mapOrderLineItems(Collection<OrderLineItem> orderLineItems) {
        return orderLineItems.stream().map(this::map).toList();
    }

    private List<OrderLineItem> mapOrderLineItemsRequest(Collection<OrderLineItemRequest> orderLineItems) {
        return orderLineItems.stream().map(this::map).toList();
    }
}

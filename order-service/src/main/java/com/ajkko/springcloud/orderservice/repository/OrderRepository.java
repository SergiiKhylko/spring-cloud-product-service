package com.ajkko.springcloud.orderservice.repository;

import com.ajkko.springcloud.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

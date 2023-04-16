package com.ajkko.springcloud.productservice.repository;

import com.ajkko.springcloud.productservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
}

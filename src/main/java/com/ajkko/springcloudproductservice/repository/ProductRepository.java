package com.ajkko.springcloudproductservice.repository;

import com.ajkko.springcloudproductservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
}

package com.ajkko.springcloudproductservice.repository;

import com.ajkko.springcloudproductservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}

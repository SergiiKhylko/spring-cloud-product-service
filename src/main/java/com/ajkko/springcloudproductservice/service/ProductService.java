package com.ajkko.springcloudproductservice.service;

import com.ajkko.springcloudproductservice.dto.mapper.ProductMapper;
import com.ajkko.springcloudproductservice.dto.request.ProductRequest;
import com.ajkko.springcloudproductservice.dto.response.ProductResponse;
import com.ajkko.springcloudproductservice.entity.Product;
import com.ajkko.springcloudproductservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public String createProduct(ProductRequest product) {
        Product createdProduct = productMapper.map(product);
        productRepository.save(createdProduct);
        log.info("Product {} is saved", createdProduct.getId());
        return createdProduct.getId();
    }

    public List<ProductResponse> getProducts() {
        return productMapper.map(
                productRepository.findAll()
        );
    }

    public ProductResponse getProduct(String id) {
        return productRepository.findById(id)
                .map(productMapper::map)
                .orElse(null);
    }

    public void removeProduct(String id) {
        productRepository.deleteById(id);
        log.info("Product {} is removed", id);
    }
}

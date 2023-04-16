package com.ajkko.springcloud.productservice.service;

import com.ajkko.springcloud.productservice.dto.mapper.ProductMapper;
import com.ajkko.springcloud.productservice.dto.request.ProductRequest;
import com.ajkko.springcloud.productservice.dto.response.ProductResponse;
import com.ajkko.springcloud.productservice.entity.Product;
import com.ajkko.springcloud.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
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

    public ProductResponse getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productMapper::map)
                .orElse(null);
    }

    public void removeProduct(String id) {
        productRepository.deleteById(id);
        log.info("Product {} is removed", id);
    }
}

package com.ajkko.springcloud.productservice.dto.mapper;

import com.ajkko.springcloud.productservice.entity.Product;
import com.ajkko.springcloud.productservice.dto.request.ProductRequest;
import com.ajkko.springcloud.productservice.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ProductMapper {
    public Product map(ProductRequest product) {
        return Product.builder()
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    public ProductResponse map(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    public List<ProductResponse> map(Collection<Product> products) {
        return products.stream().map(this::map).toList();
    }
}

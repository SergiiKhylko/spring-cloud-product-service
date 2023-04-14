package com.ajkko.springcloudproductservice.dto.mapper;

import com.ajkko.springcloudproductservice.dto.request.ProductRequest;
import com.ajkko.springcloudproductservice.dto.response.ProductResponse;
import com.ajkko.springcloudproductservice.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        return products.stream().map(this::map).collect(Collectors.toList());
    }
}

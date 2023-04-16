package com.ajkko.springcloud.productservice.dto.mapper;

import com.ajkko.springcloud.productservice.entity.Product;
import com.ajkko.springcloud.productservice.dto.request.ProductRequest;
import com.ajkko.springcloud.productservice.dto.response.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ProductMapperTest {

    private static final String VALUE_NAME = "name";
    private static final String VALUE_ID = "id";
    private static final String VALUE_DESCRIPTION = "description";
    private static final BigDecimal VALUE_PRICE = BigDecimal.TEN;

    @Test
    void shouldMap() {

        ProductMapper mapper = new ProductMapper();

        Product product = Product.builder()
                .name(VALUE_NAME)
                .description(VALUE_DESCRIPTION)
                .price(VALUE_PRICE).build();

        ProductRequest productRequest = ProductRequest.builder()
                .name(VALUE_NAME)
                .description(VALUE_DESCRIPTION)
                .price(VALUE_PRICE).build();

        ProductResponse productResponse = ProductResponse.builder()
                .id(VALUE_ID)
                .name(VALUE_NAME)
                .description(VALUE_DESCRIPTION)
                .price(VALUE_PRICE).build();

        Assertions.assertEquals(product, mapper.map(productRequest));

        product.setId(VALUE_ID);
        Assertions.assertEquals(productResponse, mapper.map(product));
    }
}
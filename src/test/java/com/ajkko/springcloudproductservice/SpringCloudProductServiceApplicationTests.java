package com.ajkko.springcloudproductservice;

import com.ajkko.springcloudproductservice.dto.mapper.ProductMapper;
import com.ajkko.springcloudproductservice.dto.request.ProductRequest;
import com.ajkko.springcloudproductservice.entity.Product;
import com.ajkko.springcloudproductservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class SpringCloudProductServiceApplicationTests {

    private static final String DOCKER_IMAGE_MONGO_VERSION = "mongo:4.4.3";
    private static final String URI_TEMPLATE = "/api/v1/products";
    private static final String CREATE_PRODUCT_NAME = "Create Product";
    private static final String GET_PRODUCTS_PRODUCT_NAME = "Get Products";
    private static final String HEADER_LOCATION = "Location";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DOCKER_IMAGE_MONGO_VERSION);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = createProductRequest(CREATE_PRODUCT_NAME);
        String productRequestStr = objectMapper.writeValueAsString(productRequest);
        long initialCount = productRepository.count();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_TEMPLATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(productRequestStr))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HEADER_LOCATION))
                .andReturn();

        Assertions.assertEquals(initialCount + 1, productRepository.count());

        Product createdProduct = productRepository.findByName(CREATE_PRODUCT_NAME)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Product %s must exist in the database", CREATE_PRODUCT_NAME)
                ));
        String createdProductId = createdProduct.getId();

        String location = Optional.ofNullable(
                mvcResult.getResponse().getHeader(HEADER_LOCATION)).orElse("");
        URI locationUri = UriComponentsBuilder.fromUriString(location).build().toUri();
        Assertions.assertEquals(locationUri.getPath(), URI_TEMPLATE + "/" + createdProductId);

        Product expectedProduct =  productMapper.map(productRequest);
        expectedProduct.setId(createdProductId);
        Assertions.assertEquals(expectedProduct, createdProduct);
        expectedProduct.setDescription(expectedProduct.getDescription() + " ");
        Assertions.assertNotEquals(expectedProduct, createdProduct);
    }

    @Test
    void shouldGetProducts() throws Exception {
        int productNumber = 10;
        List<Product> products = createTestProducts(productNumber, GET_PRODUCTS_PRODUCT_NAME);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(objectMapper.writeValueAsString(products),
                mvcResult.getResponse().getContentAsString());
    }

    private ProductRequest createProductRequest(String name, BigDecimal price) {
        return ProductRequest.builder()
                .name(name)
                .description(name + " description")
                .price(price)
                .build();
    }

    private ProductRequest createProductRequest(String name) {
        return createProductRequest(name, BigDecimal.valueOf(10000));
    }

    private List<Product> createTestProducts(int productNumber, String testName) {
        List<Product> createdProducts = new ArrayList<>();

        for (int i = 0; i < productNumber; i++) {
            ProductRequest product = createProductRequest("Product " + testName + i, BigDecimal.valueOf(1000 + i));
            createdProducts.add(productRepository.save(productMapper.map(product)));
        }
        return createdProducts;
    }
}

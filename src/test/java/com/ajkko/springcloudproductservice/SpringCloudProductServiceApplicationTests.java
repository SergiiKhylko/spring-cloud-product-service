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
    private static final String PARAM_NAME = "name";

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

        Assertions.assertEquals(objectMapper.writeValueAsString(productMapper.map(products)),
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldGetProductById() throws Exception {
        int productNumber = 10;
        List<Product> testProducts = createTestProducts(productNumber, GET_PRODUCTS_PRODUCT_NAME);
        int middleProductNumber = productNumber / 2;
        Product testProduct = testProducts.get(middleProductNumber);
        URI getProductByIdUri = UriComponentsBuilder.fromUriString(URI_TEMPLATE)
                .pathSegment(testProduct.getId())
                .build().toUri();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(getProductByIdUri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(objectMapper.writeValueAsString(productMapper.map(testProduct)),
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldNotGetProductById() throws Exception {
        int productNumber = 10;
        createTestProducts(productNumber, GET_PRODUCTS_PRODUCT_NAME);

        String WRONG_ID = "999";

        URI getProductByWrongIdUri = UriComponentsBuilder.fromUriString(URI_TEMPLATE)
                .pathSegment(WRONG_ID)
                .build().toUri();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(getProductByWrongIdUri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        Assertions.assertEquals("",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldGetProductByName() throws Exception {
        int productNumber = 10;
        List<Product> testProducts = createTestProducts(productNumber, GET_PRODUCTS_PRODUCT_NAME);
        int middleProductNumber = productNumber / 2;
        Product testProduct = testProducts.get(middleProductNumber);
        URI getProductByIdUri = UriComponentsBuilder.fromUriString(URI_TEMPLATE)
                .queryParam(PARAM_NAME, testProduct.getName())
                .build().toUri();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(getProductByIdUri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(objectMapper.writeValueAsString(productMapper.map(testProduct)),
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldNotGetProductByName() throws Exception {
        int productNumber = 10;
        String wrongName = "WRONG_NAME";
        createTestProducts(productNumber, GET_PRODUCTS_PRODUCT_NAME);
        URI getProductByIdUri = UriComponentsBuilder.fromUriString(URI_TEMPLATE)
                .queryParam(PARAM_NAME, wrongName)
                .build().toUri();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(getProductByIdUri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldDeleteProductById() throws Exception {
        int productNumber = 10;
        List<Product> testProducts = createTestProducts(productNumber, GET_PRODUCTS_PRODUCT_NAME);
        long initialNumber = productRepository.count();
        int middleProductNumber = productNumber / 2;
        Product testProduct = testProducts.get(middleProductNumber);

        URI deleteProductByIdUri = UriComponentsBuilder.fromUriString(URI_TEMPLATE)
                .pathSegment(testProduct.getId())
                .build().toUri();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(deleteProductByIdUri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(initialNumber - 1,
                productRepository.count());

        Assertions.assertEquals("",
                mvcResult.getResponse().getContentAsString());

        Assertions.assertNull(
                productRepository.findById(testProduct.getId()).orElse(null));
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

package project.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.shop.dto.product.ProductRequest;
import project.shop.dto.product.ProductResponse;
import project.shop.exception.NotFoundException;
import project.shop.mapper.ProductMapper;
import project.shop.model.Product;
import project.shop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .description("Description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();

        productRequest = ProductRequest.builder()
                .name("Test Product")
                .description("Description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .build();
    }

    @Test
    void getProductById_ShouldReturnProductResponse_WhenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(product.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct_ShouldSaveAndReturnProductResponse() {
        // Given
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // When
        ProductResponse result = productService.createProduct(productRequest);

        // Then
        assertNotNull(result);
        assertEquals(productRequest.getName(), result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}

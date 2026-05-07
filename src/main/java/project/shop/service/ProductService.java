package project.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shop.dto.product.ProductRequest;
import project.shop.dto.product.ProductResponse;
import project.shop.exception.NotFoundException;
import project.shop.mapper.ProductMapper;
import project.shop.model.Product;
import project.shop.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}

package project.shop.mapper;

import org.springframework.stereotype.Component;
import project.shop.dto.product.ProductRequest;
import project.shop.dto.product.ProductResponse;
import project.shop.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }
}

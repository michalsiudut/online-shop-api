package project.shop.mapper;

import org.springframework.stereotype.Component;
import project.shop.dto.cart.CartItemResponse;
import project.shop.dto.cart.CartResponse;
import project.shop.model.Cart;
import project.shop.model.CartItem;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponse> itemResponses = cart.getItems() != null
                ? cart.getItems().stream().map(this::toItemResponse).collect(Collectors.toList())
                : Collections.emptyList();

        BigDecimal totalCartPrice = itemResponses.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalCartPrice(totalCartPrice)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        if (item == null) {
            return null;
        }

        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }
}

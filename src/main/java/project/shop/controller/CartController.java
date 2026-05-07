package project.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.shop.dto.cart.AddToCartRequest;
import project.shop.dto.cart.CartResponse;
import project.shop.service.CartService;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // We pass userId via RequestHeader "X-User-Id" to simulate auth for now

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}

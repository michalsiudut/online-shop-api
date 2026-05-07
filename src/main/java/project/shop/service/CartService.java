package project.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shop.dto.cart.AddToCartRequest;
import project.shop.dto.cart.CartResponse;
import project.shop.exception.BadRequestException;
import project.shop.exception.NotFoundException;
import project.shop.mapper.CartMapper;
import project.shop.model.Cart;
import project.shop.model.CartItem;
import project.shop.model.Product;
import project.shop.repository.CartRepository;
import project.shop.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().userId(userId).build();
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Not enough stock for product: " + product.getName());
        }

        Optional<CartItem> existingItemOptional = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOptional.isPresent()) {
            CartItem existingItem = existingItemOptional.get();
            if (product.getStock() < existingItem.getQuantity() + request.getQuantity()) {
                throw new BadRequestException("Not enough stock to add more of: " + product.getName());
            }
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
        }

        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user"));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        cart.removeItem(itemToRemove);
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createEmptyCart(Long userId) {
        Cart cart = Cart.builder().userId(userId).build();
        return cartRepository.save(cart);
    }
}

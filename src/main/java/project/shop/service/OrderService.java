package project.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shop.dto.order.OrderRequest;
import project.shop.dto.order.OrderResponse;
import project.shop.exception.BadRequestException;
import project.shop.exception.NotFoundException;
import project.shop.mapper.OrderMapper;
import project.shop.model.Cart;
import project.shop.model.Order;
import project.shop.model.OrderItem;
import project.shop.repository.CartRepository;
import project.shop.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createFromCart(Long userId, OrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot create order from an empty cart");
        }

        Order order = Order.builder()
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status("CREATED")
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var cartItem : cart.getItems()) {
            var product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            // Reduce product stock
            product.setStock(product.getStock() - cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.addItem(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(totalAmount);

        // Status updates or saving comments from Request can be managed here.

        Order savedOrder = orderRepository.save(order);

        // Clear cart after order is placed
        cart.getItems().clear();
        cartRepository.save(cart);

        // Publish Event
        eventPublisher.publishEvent(
                new project.shop.event.OrderPlacedEvent(savedOrder.getId(), userId, savedOrder.getTotalAmount()));

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
        return orderMapper.toResponse(order);
    }
}

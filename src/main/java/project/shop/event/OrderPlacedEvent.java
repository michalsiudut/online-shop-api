package project.shop.event;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class OrderPlacedEvent {
    private final Long orderId;
    private final Long userId;
    private final BigDecimal totalAmount;

    public OrderPlacedEvent(Long orderId, Long userId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }
}

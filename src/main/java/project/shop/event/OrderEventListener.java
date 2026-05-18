package project.shop.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @Async
    @EventListener
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("--- EVENT RECEIVED ---");
        log.info("Order ID: {}", event.getOrderId());
        log.info("User ID: {}", event.getUserId());
        log.info("Total Amount: ${}", event.getTotalAmount());
        log.info("Simulating: Sending order confirmation email to the user.");
        log.info("----------------------");
    }
}

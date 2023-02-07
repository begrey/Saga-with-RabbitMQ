package com.saga.example.eventlistener;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.saga.example.event.BilledOrderEvent;
import com.saga.example.event.OrderCanceledEvent;
import com.saga.example.util.Converter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class BilledOrderEventListener {
    private static final String ROUTING_KEY = "";
    
    private final RabbitTemplate rabbitTemplate;
    private final Converter converter;
    private final String queueBilledOrderName;
    private final String topicRefundPaymentName;
    
    public BilledOrderEventListener(RabbitTemplate rabbitTemplate,
                                    Converter converter,
                                    @Value("${queue.billed-done}") String queueBilledOrderName,
                                    @Value("${topic.refund-payment}") String topicRefundPaymentName) {
    	
        this.rabbitTemplate = rabbitTemplate;
        this.converter = converter;
        this.queueBilledOrderName = queueBilledOrderName;
        this.topicRefundPaymentName = topicRefundPaymentName;
        
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBilledOrderEvent(BilledOrderEvent event) {
    	
        log.info("Sending billed done event to {}, event: {}", queueBilledOrderName, event);
        
        rabbitTemplate.convertAndSend(queueBilledOrderName, converter.toJSON(event));
        
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderCancelledEvent(OrderCanceledEvent event) {
    	
        log.info("Sending order canceled event to {}, event: {}", topicRefundPaymentName, event);
        
        rabbitTemplate.convertAndSend(topicRefundPaymentName, ROUTING_KEY, converter.toJSON(event));
        
    }
    
}
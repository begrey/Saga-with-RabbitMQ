package com.saga.example.eventhandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.saga.example.event.OrderCreatedEvent;
import com.saga.example.service.PaymentService;
import com.saga.example.util.Converter;
import com.saga.example.util.TransactionIdHolder;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@AllArgsConstructor
public class OrderCreateHandler {

    private final Converter converter;
    private final PaymentService paymentService;
    private final TransactionIdHolder transactionIdHolder;

    @RabbitListener(queues = {"${queue.order-create}"})
    public void handle(@Payload String payload) {
    	
        log.debug("Handling a created order event {}", payload);
        
        OrderCreatedEvent event = converter.toObject(payload, OrderCreatedEvent.class);
        
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        
        try {
        	paymentService.charge(event.getOrder());
        } catch (Exception e) {
        	log.error("Cannot pay billed, reason: {}", e.getMessage());
        }
        
    }
}
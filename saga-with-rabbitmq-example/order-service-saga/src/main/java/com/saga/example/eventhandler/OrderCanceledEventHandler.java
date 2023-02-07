package com.saga.example.eventhandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.saga.example.event.OrderCanceledEvent;
import com.saga.example.service.OrderService;
import com.saga.example.util.Converter;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@AllArgsConstructor
public class OrderCanceledEventHandler {

    private final Converter converter;
    
    private final OrderService orderService;

    @RabbitListener(queues = {"${queue.order-canceled}"})
    public void onOrderCanceled(@Payload String payload) {
    	
        log.info("Handling a refund order event {}", payload);
        
        OrderCanceledEvent event = converter.toObject(payload, OrderCanceledEvent.class);
        
        orderService.cancelOrder(event.getOrder().getId());
    }
}
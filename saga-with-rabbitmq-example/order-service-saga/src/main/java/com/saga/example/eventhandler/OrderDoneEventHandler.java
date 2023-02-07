package com.saga.example.eventhandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.saga.example.event.OrderDoneEvent;
import com.saga.example.service.OrderService;
import com.saga.example.util.Converter;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@AllArgsConstructor
public class OrderDoneEventHandler {

    private final Converter converter;
    
    private final OrderService orderService;

    @RabbitListener(queues = {"${queue.order-done}"})
    public void handleOrderDoneEvent(@Payload String payload) {
    	
        log.info("Handling a order done event {}", payload);
        
        OrderDoneEvent event = converter.toObject(payload, OrderDoneEvent.class);
        
        orderService.updateOrderAsDone(event.getOrder().getId());
        
    }
}
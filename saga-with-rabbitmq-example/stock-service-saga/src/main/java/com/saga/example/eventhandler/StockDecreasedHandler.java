package com.saga.example.eventhandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.saga.example.event.StockDecreaseEvent;
import com.saga.example.exception.StockException;
import com.saga.example.service.StockService;
import com.saga.example.util.Converter;
import com.saga.example.util.TransactionIdHolder;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
@Component
public class StockDecreasedHandler {

    private final Converter converter;
    private final StockService stockService;
    private final TransactionIdHolder transactionIdHolder;

    @RabbitListener(queues = {"${queue.stock-decreased}"})
    public void handle(@Payload String payload) {
    	
        log.debug("Handling a decreased stock event {}", payload);
        
        StockDecreaseEvent event = converter.toObject(payload, StockDecreaseEvent.class);
        
        transactionIdHolder.setCurrentTransactionId(event.getTransactionId());
        
        try {
        	
            stockService.updateQuantity(event.getOrder());
            
        } catch (StockException e) {
        	
            log.error("Cannot update stock, reason: {}", e.getMessage());
            
        }
    }
}
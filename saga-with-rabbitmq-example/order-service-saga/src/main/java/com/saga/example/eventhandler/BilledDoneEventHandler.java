package com.saga.example.eventhandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.saga.example.event.StockDecreaseEvent;
import com.saga.example.service.OrderService;
import com.saga.example.util.Converter;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class BilledDoneEventHandler {
    private final Converter converter;
    
    private final RabbitTemplate rabbitTemplate;
    
    private final String queueStockDecreasedName;
    
    public BilledDoneEventHandler(RabbitTemplate rabbitTemplate,
            Converter converter,
            @Value("${queue.stock-decreased}") String queueStockDecreasedName) {

		this.rabbitTemplate = rabbitTemplate;
		this.converter = converter;
		this.queueStockDecreasedName = queueStockDecreasedName;
		
	}

    
    @RabbitListener(queues = {"${queue.billed-done}"})
    public void onOrderCanceled(@Payload String payload) {
    	
        log.info("receive billed-done event from Payment Service {}", payload);
        
        StockDecreaseEvent event = converter.toObject(payload, StockDecreaseEvent.class);
        rabbitTemplate.convertAndSend(queueStockDecreasedName, converter.toJSON(event));
    }
}

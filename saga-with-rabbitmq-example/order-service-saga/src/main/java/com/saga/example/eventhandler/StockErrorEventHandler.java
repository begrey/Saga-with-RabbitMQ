package com.saga.example.eventhandler;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.saga.example.event.RefundPaymentEvent;
import com.saga.example.util.Converter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockErrorEventHandler {
    private final Converter converter;
    
    private final RabbitTemplate rabbitTemplate;
    
    private final String queueRefundPaymentName;

    public StockErrorEventHandler(RabbitTemplate rabbitTemplate,
            Converter converter,
            @Value("${queue.refund-payment}") String queueRefundPaymentName) {

		this.rabbitTemplate = rabbitTemplate;
		this.converter = converter;
		this.queueRefundPaymentName = queueRefundPaymentName;
		
	}
    
    
    @RabbitListener(queues = {"${queue.stock-error}"})
    public void onOrderCanceled(@Payload String payload) {
    	
        log.info("Send StockError Message To Stock Service {}", payload);
        
        RefundPaymentEvent event = converter.toObject(payload, RefundPaymentEvent.class);
        rabbitTemplate.convertAndSend(queueRefundPaymentName, converter.toJSON(event));
    }
}

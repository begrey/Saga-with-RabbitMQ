package com.saga.example.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saga.example.event.BilledOrderEvent;
import com.saga.example.event.OrderCanceledEvent;
import com.saga.example.exception.ChargeException;
import com.saga.example.model.Order;
import com.saga.example.model.Payment;
import com.saga.example.repository.PaymentRepository;
import com.saga.example.util.TransactionIdHolder;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher publisher;
    private final TransactionIdHolder transactionIdHolder;

    @Transactional
    public void charge(Order order) {
    	
    	confirmCharge(order);

        log.debug("Charging order {}", order);
        
        Payment payment = createPayment(order);

        log.debug("Saving payment {}", payment);
        
        paymentRepository.save(payment);

        publish(order);
        
    }

    private void confirmCharge(Order order) {
    	
        log.debug("Confirm charge for order id {}", order.getId());
        
        /*
         * Business logic
         * ...
         */
        
        boolean confirm = true;
        if (order.getCost() > 10000)  { // 예산초과
        	confirm = false;
        }
        
        if (confirm) {
        	
            log.info("Charge confirmed for order id {}", order.getId()); 
            
        	return;
        	
        } else {
        	
            publishCanceledOrder(order);
            
            throw new ChargeException("Order id " + order.getId());
            
        }
    }
    
    private void publish(Order order) {
    	
        BilledOrderEvent billedOrderEvent = new BilledOrderEvent(transactionIdHolder.getCurrentTransactionId(), order);
        
        log.debug("Publishing a billed order event {}", billedOrderEvent);
        
        publisher.publishEvent(billedOrderEvent);
        
    }

    private Payment createPayment(Order order) {
    	
        return Payment.builder()
                .paymentStatus(Payment.PaymentStatus.BILLED)
                .valueBilled(order.getCost())
                .orderId(order.getId())
                .build();
        
    }

    
    private void publishCanceledOrder(Order order) {
    	
        OrderCanceledEvent event = new OrderCanceledEvent(transactionIdHolder.getCurrentTransactionId(), order);
        
        log.debug("Publishing canceled order event {}", event);
        
        publisher.publishEvent(event);
        
    }
    
    @Transactional
    public void refund(Long orderId) {
    	
        log.debug("Refund Payment by order id {}", orderId);
        
        Optional<Payment> paymentOptional = paymentRepository.findByOrderId(orderId);
        
        if (paymentOptional.isPresent()) {
        	
            Payment payment = paymentOptional.get();
            payment.setPaymentStatus(Payment.PaymentStatus.REFUND);
            paymentRepository.save(payment);
            
            log.debug("Payment {} was refund", payment.getId());
            
        } else {
        	
            log.error("Too Expensive :( Payment is not created by order id {}", orderId);
            
        }
    }
}
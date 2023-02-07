package com.saga.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saga.example.model.Order;
import com.saga.example.service.OrderService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
    	
        log.debug("Creating a new {}", order);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(order));
        
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
    	
        return ResponseEntity.ok().body(service.findAll());
        
    }
}
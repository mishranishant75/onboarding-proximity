package com.example.demo.controller;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.feignClient.ItemServiceClient;

import com.example.demo.entity.ItemEntity;
@RestController
@RequestMapping("/feignapi")
public class Controller {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ItemServiceClient itemServiceClient;
	
	@Autowired
	Resilience4JCircuitBreakerFactory circuitBreakerFactory;
	
	@Autowired
	StreamBridge streamBridge;
	
	@Autowired
	ExecutorService traceableExecutorService; 
	
	@GetMapping("/hello")	
	public String hello() {
		String hello = itemServiceClient.getHello();
		return hello;
	}
	
	@GetMapping("/feign")
	public ResponseEntity<List<ItemEntity>>getAll() {
		ResponseEntity<List<ItemEntity>> allItems = itemServiceClient.getAllItems();
		return allItems;
	}
	
	@GetMapping("/feign/{id}")
	public ResponseEntity<ItemEntity> getItemsById(@PathVariable("id") int id) {
		circuitBreakerFactory.configureExecutorService(traceableExecutorService);
		Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("item");
		log.info("making rest call to item service for id "+id);
		Supplier<ResponseEntity<ItemEntity>> supplier = () -> itemServiceClient.getItemById(id);
		ResponseEntity<ItemEntity> item = circuitBreaker.run(supplier, thorwable -> handleErrorCase());
		
		boolean sent = streamBridge.send("feignEventSupplier-out-0","hi from feign service");
		log.info("msg sent on stream bridge is "+sent);
		return item;
	}
	
	private ResponseEntity<ItemEntity> handleErrorCase(){
		ItemEntity item = new ItemEntity();
		item.setId(0);
		item.setName("default value from circuit breaker");
		return new ResponseEntity<ItemEntity>(item, HttpStatus.OK);	
	}
	
	@DeleteMapping("/feign/{id}")
	public ResponseEntity<ItemEntity> deleteItemById(@PathVariable("id") int id) {
		ResponseEntity<ItemEntity> response = itemServiceClient.deleteItemById(id);
		return response;
	}
		
	@PostMapping("/feign")
	public ResponseEntity<ItemEntity> createItem(@RequestBody ItemEntity itemEntity) {
		ResponseEntity<ItemEntity> response = itemServiceClient.createItem(itemEntity);
		return response;
	}
	
	@PutMapping("/feign/{id}")
	public ResponseEntity<ItemEntity> updateItem(@PathVariable("id") int id, @RequestBody ItemEntity itemEntity) {
		ResponseEntity<ItemEntity> response = itemServiceClient.updateItem(id, itemEntity);
		return response;
	}
	
}

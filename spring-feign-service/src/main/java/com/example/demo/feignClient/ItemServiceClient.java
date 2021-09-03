package com.example.demo.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.ItemEntity;

@FeignClient(name = "item")
public interface ItemServiceClient {

	@GetMapping("/itemapi/hello")
	public String getHello();
	
	@GetMapping("/itemapi/item")
	public ResponseEntity<List<ItemEntity>> getAllItems();
	
	@GetMapping("/itemapi/item/{id}")
	public ResponseEntity<ItemEntity> getItemById(@PathVariable("id") int id);
	
	@DeleteMapping("/itemapi/item/{id}")
	public ResponseEntity<ItemEntity> deleteItemById(@PathVariable("id") int id);
	
	@PostMapping("/itemapi/item")
	public ResponseEntity<ItemEntity> createItem(@RequestBody ItemEntity itemEntity);
	
	@PutMapping("/itemapi/item/{id}")
	public ResponseEntity<ItemEntity> updateItem(@PathVariable("id") int id, @RequestBody ItemEntity itemEntity);
}

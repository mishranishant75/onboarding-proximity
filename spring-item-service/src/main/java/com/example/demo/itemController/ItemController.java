package com.example.demo.itemController;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.itemEntity.ItemEntity;
import com.example.demo.itemService.itemService;

@RestController
@RequestMapping("/itemapi")
public class ItemController {
		
	@Autowired
	private itemService itemService;
	
//	@Autowired
//    Configuration configuration;
	
	@GetMapping("/item")
	public ResponseEntity<List<ItemEntity>> getAllItems() {
		return itemService.getAllItems();
	}
	
	@GetMapping("/item/{id}")
	public ResponseEntity<ItemEntity> getItemById(@PathVariable("id") int id) {
		return itemService.getItemById(id);
	}
	
	@DeleteMapping("/item/{id}")
	public ResponseEntity<HttpStatus> deleteItemById(@PathVariable("id") int id) {
		return itemService.deleteItemById(id);
	}
	
	@PostMapping("/item")
	public ResponseEntity<ItemEntity> createItem(@RequestBody ItemEntity itemEntity) {
		return itemService.createItem(itemEntity);
	}
	
	@PutMapping("/item/{id}")
	public ResponseEntity<ItemEntity> updateItem(@PathVariable("id")int id, @RequestBody ItemEntity itemEntity) {
		return itemService.updateItem(id, itemEntity);
	}
	
	@RequestMapping("/hello")
	public String hello() {
		return "hello from item service";
	}
	
//	@GetMapping("/msg")
//    public String retrieveConfig(){
//        return configuration.getValue();
//    }
}

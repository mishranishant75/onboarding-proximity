package com.example.demo.itemService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.itemEntity.ItemEntity;

public interface itemService {
	public ResponseEntity<List<ItemEntity>> getAllItems();
	public ResponseEntity<ItemEntity> getItemById(int id);
	public ResponseEntity<HttpStatus> deleteItemById(int id);
	public ResponseEntity<ItemEntity> createItem(ItemEntity itemEntity);
	public ResponseEntity<ItemEntity> updateItem(int id, @RequestBody ItemEntity itemEntity);
}

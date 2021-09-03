package com.example.demo.itemServiceImp;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import com.example.demo.itemEntity.ItemEntity;
import com.example.demo.itemRepo.ItemRepo;
import com.example.demo.itemService.itemService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class itemServiceImp implements itemService{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ItemRepo itemRepo;

	@Override
	public ResponseEntity<List<ItemEntity>> getAllItems() {
		try {	
			List<ItemEntity> list = itemRepo.findAll();
			if (list.isEmpty()) {
				log.info("list empty while retrieving all items");
		        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		      }
			log.info("retrieved all items");
			return new ResponseEntity<List<ItemEntity>>(list, HttpStatus.OK);		
		} catch (Exception e) {
			log.info("Exception while retrieving all items");	
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@Override
	public ResponseEntity<ItemEntity> getItemById(int id) {
		Optional<ItemEntity> item = itemRepo.findById(id);
		if (item.isPresent()) {
			log.info("item found with id "+id);
			return new ResponseEntity<>(item.get(), HttpStatus.OK);
		} else {
			log.info("item not found with id "+id);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<HttpStatus> deleteItemById(int id) {
		try {
		      itemRepo.deleteById(id);
		      log.info("item deleted with id "+id);
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    } catch (Exception e) {
		    	log.info("exception while deleting item with id "+id);
		      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	}

	@Override
	public ResponseEntity<ItemEntity> createItem(ItemEntity itemEntity) {
		try {
			ItemEntity _item = itemRepo.save(new ItemEntity(itemEntity.getName()));
			log.info("item saved with id "+itemEntity.getId());
		    return new ResponseEntity<>(_item, HttpStatus.CREATED);
		} catch (Exception e) {
			log.info("exception while saving item with id "+itemEntity.getId());
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
	}

	@Override
	public ResponseEntity<ItemEntity> updateItem(int id, ItemEntity itemEntity) {
		Optional<ItemEntity> item = itemRepo.findById(id);
		if (item.isPresent()) {
		      ItemEntity _item = item.get();
		      _item.setName(itemEntity.getName());
		      return new ResponseEntity<>(itemRepo.save(_item), HttpStatus.OK);
		    } else {
		      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		    }
	}
	
}

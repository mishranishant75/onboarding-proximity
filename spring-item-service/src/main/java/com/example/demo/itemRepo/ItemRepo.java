package com.example.demo.itemRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.demo.itemEntity.ItemEntity;

public interface ItemRepo extends JpaRepository<ItemEntity, Integer> {

}

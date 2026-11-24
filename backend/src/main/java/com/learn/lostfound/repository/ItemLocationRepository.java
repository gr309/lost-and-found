package com.learn.lostfound.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lostfound.entity.Item;
import com.learn.lostfound.entity.ItemLocation;

public interface  ItemLocationRepository extends JpaRepository<ItemLocation, Long> {
    Optional<ItemLocation> findByItem(Item item); 
}

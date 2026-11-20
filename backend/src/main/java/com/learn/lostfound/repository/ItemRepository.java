package com.learn.lostfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lostfound.entity.Item;

public interface  ItemRepository extends JpaRepository<Item, Long> {
    
}

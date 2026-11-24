package com.learn.lostfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lostfound.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}

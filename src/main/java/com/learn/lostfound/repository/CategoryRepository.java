package com.learn.lostfound.repository;
import com.learn.lostfound.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface  CategoryRepository  extends  JpaRepository<Category, Long>{
    
}

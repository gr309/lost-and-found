package com.learn.lostfound.controller;
import java.util.List;

import  org.springframework.web.bind.annotation.*;

import com.learn.lostfound.service.CategoryService;
import com.learn.lostfound.entity.Category;




@RestController 
@RequestMapping ("/api/categories/")
public class CategoryController {
    private final CategoryService categoryService; 
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService; 
    }

    @GetMapping("/")
    public List<Category> getCategories() {
        return categoryService.getAllCategories(); 
    }

    // @GetMapping("/{id}")
    // public Category getCategory(@PathVariable Long id) {
    //     return categoryService.get
    // }

    @PostMapping("/")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }
    
    
}

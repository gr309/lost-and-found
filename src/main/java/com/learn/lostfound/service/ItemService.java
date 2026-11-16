package com.learn.lostfound.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learn.lostfound.dto.ItemRequest;
import com.learn.lostfound.dto.ItemResponse;
import com.learn.lostfound.repository.CategoryRepository;
import com.learn.lostfound.repository.ItemRepository;
import com.learn.lostfound.entity.*;


import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service 
public class ItemService {
    private final ItemRepository itemRepository; 
    private final CategoryRepository categoryRepository; 

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;  
    }

    // Create item 
    @Transactional 
    public ItemResponse createItem(ItemRequest request) {

        Category category = categoryRepository
            .findById(request.categoryId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Category not found"
            ));

        Item item = new Item();

        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setType(request.type());
        item.setCategory(category);
        item.setLocation(request.location());
        item.setEventDate(request.eventDate());

        Item savedItem = itemRepository.save(item);

        return toResponse(savedItem);
    }
    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {

        return itemRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    // Convert entity to response DTO
    private ItemResponse toResponse(Item item) {

        return new ItemResponse(
            item.getId(),
            item.getTitle(),
            item.getDescription(),
            item.getType(),
            item.getCategory().getId(),
            item.getCategory().getName(),
            item.getLocation(),
            item.getEventDate(),
            item.getCreatedAt()
        );
    }
}

package com.learn.lostfound.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.learn.lostfound.dto.ItemRequest;
import com.learn.lostfound.dto.ItemResponse;
import com.learn.lostfound.service.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> getItems() {
        return itemService.getAllItems();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) 
    {
        return itemService.createItem(request);
    }
}
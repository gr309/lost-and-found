package com.learn.lostfound.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.learn.lostfound.dto.ItemRequest;
import com.learn.lostfound.dto.ItemResponse;
import com.learn.lostfound.service.ItemService;
import org.springframework.http.MediaType;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items/")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public List<ItemResponse> getItems() {
        return itemService.getAllItems();
    }

    @GetMapping("{id}/")
    public ItemResponse getItem(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return itemService.getItems(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(
            @Valid @ModelAttribute ItemRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {

        Long userId = Long.valueOf(jwt.getSubject());
        return itemService.createItem(request, image, userId);
    }


    // @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @ResponseStatus(HttpStatus.CREATED)
    // public ItemResponse createItem(
    //         @Valid @RequestPart("data") ItemRequest request,
    //         @RequestPart(value = "image", required = false)
    //         MultipartFile image,
    //         @AuthenticationPrincipal Jwt jwt
    // ) throws IOException {

    //     Long userId = Long.valueOf(jwt.getSubject());
    //     return itemService.createItem(request, image, userId);
    // }


    // @PostMapping("/")
    // @ResponseStatus(HttpStatus.CREATED)
    // public ItemResponse createItem(@Valid @RequestBody ItemRequest request, @AuthenticationPrincipal Jwt jwt) {
    //     Long userId = Long.valueOf(jwt.getSubject()); 
    //     return itemService.createItem(request, userId);
    // }

    @PutMapping("{id}/")
    public ItemResponse updatedItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        return itemService.updateItem(id, request, userId); 
    }

    @DeleteMapping ("{id}/")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject()); 
        itemService.deleteItem(id, userId);

    }

}
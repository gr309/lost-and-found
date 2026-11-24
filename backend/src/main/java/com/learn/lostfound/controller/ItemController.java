package com.learn.lostfound.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*; 

import com.learn.lostfound.dto.ItemDTO;
import com.learn.lostfound.service.ItemService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/items/")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public List<ItemDTO.Response.Summary> getItems() {
        return itemService.getAllItems();
    }

    @GetMapping("{id}/")
    public ResponseEntity<?> getItem(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = ((jwt != null) ? Long.valueOf(jwt.getSubject()): null);
        return ResponseEntity.ok(itemService.getItem(id, userId));
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO.Response.OwnerDetail createItem(
            @Valid @RequestBody ItemDTO.Request.Save request,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {

        Long userId = Long.valueOf(jwt.getSubject());
        return itemService.createItem(request, userId);
    }
    
    @PutMapping("{id}/")
    public ItemDTO.Response.OwnerDetail updatedItem(
        @PathVariable Long id, 
        @Valid @RequestBody ItemDTO.Request.Save request, 
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());

        return itemService.updateItem(id, request, userId); 
    }


    @PostMapping("{id}/location/")
    @ResponseStatus (HttpStatus.CREATED)
    public Object createLocation(
        @RequestBody ItemDTO.Request.Location request, 
        @PathVariable Long id,  
        @AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.valueOf(jwt.getSubject()); 
        

        return itemService.createLocation(id, request, userId); 
    }
    @PutMapping ("{id}/location/")
    public Object updateLocation(
        @RequestBody ItemDTO.Request.Location request, 
        @PathVariable Long id,  
        @AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.valueOf(jwt.getSubject()); 
        
        return itemService.createLocation(id, request, userId); 
    }

    @PostMapping("{id}/images/")
    public Object addImage(
        @PathVariable Long id
    ) {


        return null;
    }
}
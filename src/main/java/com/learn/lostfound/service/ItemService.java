package com.learn.lostfound.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.learn.lostfound.dto.ItemRequest;
import com.learn.lostfound.dto.ItemResponse;
import com.learn.lostfound.repository.ItemRepository;
import com.learn.lostfound.repository.UserRepository;
import com.learn.lostfound.entity.*;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service 
public class ItemService {
    private final ItemRepository itemRepository; 
    private final UserRepository userRepository; 

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository; 
    }

    // Create item 
    @Transactional 
    public ItemResponse createItem(ItemRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
                )
            );
        Item item = new Item();

        item.setUser(user);
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setType(request.type());
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


    @Transactional(readOnly=true)
    public ItemResponse getItems(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Item not found"
                )
            );
        return toResponse(item); 
    }
    
    // UPDATE ITEM
    @Transactional 
    public ItemResponse updateItem(Long id, ItemRequest request, Long userId) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found"
                )
            );

        if(!item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot update this item"
            );
        }
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setType(request.type());
        item.setLocation(request.location());
        item.setEventDate(request.eventDate());

        Item updatedItem = itemRepository.save(item);

        return toResponse(updatedItem);
    }

    @Transactional
    public void deleteItem(Long id, Long userId) {

        Item item = itemRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found"
                )
            );
        if (!item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot delete this item"
            );
        }

        itemRepository.delete(item);
    }


    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
            item.getId(),
            item.getTitle(),
            item.getDescription(),
            item.getType(),
            item.getLocation(),
            item.getEventDate(),
            item.getCreatedAt()
        );
    }
}

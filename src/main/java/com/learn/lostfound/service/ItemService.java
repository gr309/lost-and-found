package com.learn.lostfound.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.learn.lostfound.dto.ItemRequest;
import com.learn.lostfound.dto.ItemResponse;
import com.learn.lostfound.repository.ItemRepository;
import com.learn.lostfound.repository.UserRepository;
import com.learn.lostfound.entity.*;
// import com.learn.lostfound.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.io.IOException;

@Service 
public class ItemService {
    private final ItemRepository itemRepository; 
    private final UserRepository userRepository; 
    private final FileStorageService fileStorageService;


    public ItemService(ItemRepository itemRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    // Create item 


    @Transactional 
    public ItemResponse createItem(
        ItemRequest request,
        MultipartFile image, 
        Long userId
    ) throws IOException {
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

        if(image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.save(image);
            item.setImageUrl(imageUrl);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) deleteImageQuietly(imageUrl);
                }
            });
        }

        Item savedItem = itemRepository.save(item);

        return toResponse(savedItem);
    }

    // @Transactional 
    // public ItemResponse createItem(ItemRequest request,  Long userId) {
    //     User user = userRepository.findById(userId)
    //         .orElseThrow(() ->
    //             new ResponseStatusException(
    //                 HttpStatus.UNAUTHORIZED,
    //                 "User not found"
    //             )
    //         );
    //     Item item = new Item();

    //     item.setUser(user);
    //     item.setTitle(request.title());
    //     item.setDescription(request.description());
    //     item.setType(request.type());
    //     item.setLocation(request.location());
    //     item.setEventDate(request.eventDate());

    //     Item savedItem = itemRepository.save(item);

    //     return toResponse(savedItem);
    // }


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
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteImageQuietly(item.getImageUrl());
            }
        });
    }

    private void deleteImageQuietly(String imageUrl) {
        try {
            fileStorageService.delete(imageUrl);
        } catch (IOException ex) {
            org.slf4j.LoggerFactory.getLogger(ItemService.class).warn("Unable to remove unused image {}", imageUrl, ex);
        }
    }


    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
            item.getId(),
            item.getTitle(),
            item.getDescription(),
            item.getType(),
            item.getLocation(),
            item.getEventDate(),
            item.getImageUrl(), 
            item.getCreatedAt()
        );
    }
}

package com.learn.lostfound.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.learn.lostfound.dto.ItemDTO;
import com.learn.lostfound.repository.*;
import com.learn.lostfound.entity.*;
// import com.learn.lostfound.service.FileStorageService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.io.IOException;


@Service 
public class ItemService {
    private final ItemRepository itemRepository; 
    private final UserRepository userRepository; 
    private final CategoryRepository categoryRepository; 
    private final ItemLocationRepository itemLocationRepository; 
    


    public ItemService(
        ItemRepository itemRepository, 
        UserRepository userRepository, 
        CategoryRepository categoryRepository, 
        ItemLocationRepository itemLocationRepository
    ) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.itemLocationRepository = itemLocationRepository;
    }

    // Create item 
    @Transactional 
    public ItemDTO.Response.OwnerDetail createItem(
        ItemDTO.Request.Save request,
        Long userId
    ) throws IOException {
        User user = userRepository.findById(userId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
                )
            );

        Category category = categoryRepository.findById(request.categoryId())
                            .orElseThrow(() -> 
                                new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, 
                                    "Invalid Category."
                                )
                        );
        Item item = new Item();

        item.setReporter(user);
        item.setCategory(category);
        item.setType(request.type());
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setPrivateDescription(request.privateDescription());

        
        item.setEventDate(request.eventDate());
        item.setEventTime(request.eventTime());
        item.setTimeMarginMinutes(request.timeMarginMinutes());
        
        Item savedItem = itemRepository.save(item);

        return toOwnerDetail(savedItem);
    }


    @Transactional(readOnly = true)
    public List<ItemDTO.Response.Summary> getAllItems() {
        return itemRepository.findAll()
            .stream()
            .map(this::toSummary)
            .toList();
    }

    
    @Transactional (readOnly = true) 
    public Object getItem(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Item not found"
                )
            );    
        if (userId != null && item.getReporter().getId().equals(userId)) {
            return toOwnerDetail(item);
        }
        return toDetail(item); 
        
    }

    // UPDATE ITEM
    @Transactional
    public ItemDTO.Response.OwnerDetail updateItem(
            Long id,
            ItemDTO.Request.Save request,
            Long userId
    ) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found"
                )
            );

        // Check ownership
        if (!item.getReporter().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot update this item"
            );
        }

        // Validate category
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid category"
                )
            );

        // Validate time
        if (request.eventTime() == null
                && request.timeMarginMinutes() != null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Time margin requires an event time"
            );
        }

        // Update fields
        item.setCategory(category);
        item.setType(request.type());
        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setPrivateDescription(request.privateDescription());
        item.setEventDate(request.eventDate());
        item.setEventTime(request.eventTime());
        item.setTimeMarginMinutes(request.timeMarginMinutes());

        // Save
        Item updatedItem = itemRepository.save(item);

        return toOwnerDetail(updatedItem);
    }

    @Transactional 
    public Object createLocation(long itemId, ItemDTO.Request.Location request, Long userId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found"
                )
            );

        if (!item.getReporter().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot create locatio of this item."
            );
        }

        ItemLocation location = new ItemLocation(); 

        location.setItem(item);
        location.setDivision(request.division());
        location.setDistrict(request.district());
        location.setUpazila(request.upazila());
        location.setArea(request.area());
        location.setAddressText(request.addressText());
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setPrecision(request.precision());
        location.setUncertaintyRadiusMeters(request.uncertaintyRadiusMeters());

        itemLocationRepository.save(location); 
        

        return null;
    }


    @Transactional 
    public Object updateLocation(long itemId, ItemDTO.Request.Location request, Long userId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found"
                )
            );

        if (!item.getReporter().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot create locatio of this item."
            );
        }

        ItemLocation location = itemLocationRepository
                            .findByItem(item).orElseThrow(() -> 
                                new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, 
                                    "Location not found"
                                )
                        );


        location.setDivision(request.division());
        location.setDistrict(request.district());
        location.setUpazila(request.upazila());
        location.setArea(request.area());
        location.setAddressText(request.addressText());
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setPrecision(request.precision());
        location.setUncertaintyRadiusMeters(request.uncertaintyRadiusMeters());

        itemLocationRepository.save(location); 

        return null;
    }













    private ItemDTO.Response.Summary toSummary(Item item) {
        return new ItemDTO.Response.Summary(
            item.getId(),
            item.getType(),
            item.getTitle(),
            item.getCategory().getId(),
            item.getStatus(),
            item.getEventDate(),
            item.getCreatedAt()
        );
    }
    private ItemDTO.Response.Detail toDetail(Item item) {
        return new ItemDTO.Response.Detail(
            item.getId(),
            item.getReporter().getId(),
            item.getCategory().getId(),
            item.getType(),
            item.getTitle(),
            item.getDescription(),
            item.getStatus(),
            item.getEventDate(),
            item.getEventTime(),
            item.getTimeMarginMinutes(),
            item.getCreatedAt()
        );
    }
    private ItemDTO.Response.OwnerDetail toOwnerDetail(Item item) {
        return new ItemDTO.Response.OwnerDetail(
            item.getId(),
            item.getReporter().getId(),
            item.getCategory().getId(),
            item.getType(),
            item.getTitle(),
            item.getDescription(),
            item.getPrivateDescription(), 
            item.getStatus(),
            item.getEventDate(),
            item.getEventTime(),
            item.getTimeMarginMinutes(),
            item.getCreatedAt()
        );
    }
}

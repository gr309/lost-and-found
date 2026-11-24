package com.learn.lostfound.entity;

import com.learn.lostfound.enums.ItemEnums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_images")
public class ItemImage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, length = 500)
    private String storageKey;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemEnums.Visibility visibility = ItemEnums.Visibility.PUBLIC;

    public ItemImage() {}
}
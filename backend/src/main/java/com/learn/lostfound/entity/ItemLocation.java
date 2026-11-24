package com.learn.lostfound.entity;

import java.math.BigDecimal;

import com.learn.lostfound.enums.ItemEnums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_locations")
public class ItemLocation extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "item_id",
        nullable = false,
        unique = true
    )
    private Item item;

    @Column(length = 100)
    private String division;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String upazila;

    @Column(length = 200)
    private String area;

    @Column(length = 500)
    private String addressText;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemEnums.LocationPrecision precision;

    private Integer uncertaintyRadiusMeters;

    public ItemLocation() {}
}
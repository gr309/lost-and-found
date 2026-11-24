package com.learn.lostfound.entity;
import java.time.LocalDate;
import java.time.LocalTime;

import com.learn.lostfound.enums.ItemEnums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter 
@Entity 
@Table (name="items")
public class Item extends BaseEntity{
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="reporter_id", nullable = false)
    private User reporter; 
    
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="category_id", nullable = false)
    private Category category; 

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 30) 
    private ItemEnums.Type type;  

    @Column (nullable=false)
    private String title; 

    @Column (nullable = false, columnDefinition = "TEXT")
    private String description; 

    @Column (columnDefinition = "TEXT")
    private String privateDescription; 

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 30)
    private ItemEnums.Status status = ItemEnums.Status.ACTIVE; 

    @Column (nullable = false)
    private LocalDate eventDate; 

    @Column ()
    private LocalTime eventTime; 

    @Column ()
    private Integer timeMarginMinutes; 

    public Item() {}
}

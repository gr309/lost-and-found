package com.learn.lostfound.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Entity 
public class Category extends BaseEntity{
    @Column (nullable = false, length = 50)
    private String name; 

    @ManyToOne 
    @JoinColumn (name = "parent_id")
    private Category parent = null; 

    @Column (nullable = false, unique = true)
    private String slug;

    @Column (nullable = false)
    private Boolean isActive = true; 
}
